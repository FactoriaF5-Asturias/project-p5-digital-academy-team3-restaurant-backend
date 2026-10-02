# Docker en el backend

El backend se puede ejecutar entero en Docker: la base de datos y la API. Hay tres ficheros implicados.

| Fichero | Qué hace |
|---|---|
| `Dockerfile` | Construye la imagen de la API |
| `.dockerignore` | Deja fuera de la imagen `target/`, `.env`, `.git`, `.claude`, `docs/` y los `.md` |
| `docker-compose.yaml` | Levanta PostgreSQL y, si se pide, la API |

## Dockerfile

Tiene dos etapas.

1. `build`, sobre `eclipse-temurin:21-jdk`: descarga las dependencias con `./mvnw dependency:go-offline` y compila el jar con `./mvnw package -DskipTests`. Las dependencias se copian antes que `src/`, así que Docker las reutiliza de la caché mientras no cambie el `pom.xml`.
2. Imagen final, sobre `eclipse-temurin:21-jre`: copia solo el jar y lo arranca con `java -jar app.jar`. No lleva Maven ni el código fuente, y se ejecuta con el usuario `giacobello`, no como root.

Los tests no se pasan al construir la imagen porque necesitan Docker (Testcontainers). Se pasan antes con `./mvnw clean verify`.

## docker-compose.yaml

| Servicio | Contenedor | Puerto | Cuándo arranca |
|---|---|---|---|
| `postgres` | `giacobello-postgres` | 5432 | Siempre |
| `app` | `giacobello-app` | 8080 | Solo con `--profile app` |

El servicio `app` está detrás del perfil `app` para no cambiar el flujo de desarrollo: `docker compose up -d` sigue levantando solo la base de datos y la API se arranca desde el IDE. Con el perfil, Compose construye la imagen y espera a que PostgreSQL esté `healthy` antes de arrancar la API.

Variables que recibe `app` (salen del `.env`):

| Variable | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | La del `.env`, o `dev` si no está |
| `SPRING_DATASOURCE_URL` y `DATABASE_URL` | `jdbc:postgresql://postgres:5432/giacobello`, el nombre del servicio dentro de la red de Compose |
| `DATABASE_USERNAME` y `DATABASE_PASSWORD` | Las del `.env` |
| `TZ` | `Europe/Madrid`, para que pedidos y facturas guarden la hora de España |
| `SUPABASE_URL`, `SUPABASE_SERVICE_KEY`, `SUPABASE_REPORTS_BUCKET`, `SALES_REPORT_ARCHIVE_CRON` | Para el resumen de ventas automático. Vacías, la tarea no se activa |

## Comandos

```bash
docker compose up -d                          # solo PostgreSQL
docker compose --profile app up -d --build    # PostgreSQL y API, reconstruyendo la imagen
docker compose --profile app logs -f app      # ver los logs de la API
docker compose --profile app down             # parar todo
docker compose down -v                        # parar y borrar los datos de PostgreSQL
```

`--build` hace falta cada vez que cambia el código. Sin él, Compose reutiliza la imagen anterior.

Antes de levantar la API en Docker hay que parar la que esté corriendo en el IDE, porque las dos usan el puerto 8080.

`docker compose down -v` borra el volumen `giacobello-pgdata`. Al volver a arrancar, Flyway crea las tablas y carga los datos de ejemplo desde cero.
