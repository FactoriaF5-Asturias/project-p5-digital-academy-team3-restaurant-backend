# Giacobello

Backend de la aplicación de restaurante Giacobello. Expone una API para consultar el catálogo y gestionar pedidos, métodos de pago e información relacionada con las facturas.

## ¿Qué es?

Giacobello es el servicio que conecta la aplicación del restaurante con sus datos. Recibe peticiones de otros clientes —por ejemplo, una interfaz web— y trabaja con la información guardada en PostgreSQL.

## ¿Para qué sirve?

Con la API se pueden consultar productos, categorías, mesas y métodos de pago; crear pedidos y actualizar su estado; y consultar facturas e informes. También incluye inicio de sesión con tokens y operaciones de administración para el catálogo.

La API aplica permisos según el tipo de operación, por lo que algunos endpoints requieren autenticación. La lista de rutas y sus ejemplos está en [`docs/endpoints.md`](docs/endpoints.md).

## Instalación y arranque

### Requisitos

- Docker con Docker Compose.
- Java 21, solo si vas a arrancar la app fuera de Docker o a pasar los tests.

### Variables de entorno

Copia la plantilla y pon tus credenciales:

```bash
cp .env.example .env
```

| Variable | Para qué sirve |
|---|---|
| `DATABASE_USERNAME` | Usuario de PostgreSQL |
| `DATABASE_PASSWORD` | Contraseña de PostgreSQL |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring: `dev` en local |
| `JWT_KEY` | Clave para firmar los tokens JWT (mínimo 64 bytes, HS512). Genérala con `openssl rand -base64 64` |
| `SUPABASE_URL` | URL del proyecto de Supabase. Opcional |
| `SUPABASE_SERVICE_KEY` | Clave `service_role` de Supabase. Opcional |

### Resumen de ventas automático

Con `SUPABASE_URL` y `SUPABASE_SERVICE_KEY` rellenas, el backend genera cada noche a las 00:05 (hora de Madrid) el PDF de ventas del día anterior y lo sube al bucket `sales-reports` de Supabase Storage como `daily/sales-report-AAAA-MM-DD.pdf`. El bucket tiene que existir y ser privado. Si las variables están vacías, la tarea no se activa y el resto de la app funciona igual. Si rellenas `SUPABASE_URL` pero no la clave, la app no arranca: rellena las dos o ninguna. Si una noche falla la subida, queda en el log como `No se pudo archivar el resumen de ventas` y no se reintenta.

La hora se cambia con `SALES_REPORT_ARCHIVE_CRON` (expresión cron de Spring) y el bucket con `SUPABASE_REPORTS_BUCKET`.

Para subirlo a mano sin esperar a la noche (por ejemplo, en una demo): `POST /api/v1/invoices/report/archive?date=AAAA-MM-DD`. Sin `date` sube el de ayer. Devuelve 503 si Supabase no está configurado y 502 si falla la subida.

### Opción A: todo en Docker

Levanta PostgreSQL y el backend:

```bash
docker compose --profile app up -d --build
```

La API queda en `http://localhost:8080/api/v1` y Swagger en `http://localhost:8080/swagger-ui/index.html`. Flyway crea las tablas y carga los datos de ejemplo al arrancar.

Para pararlo: `docker compose --profile app down`. Cómo están montados la imagen y el compose: [docs/docker.md](docs/docker.md).

### Opción B: base de datos en Docker y backend en local

Útil para desarrollar, porque la app se reinicia desde el IDE:

```bash
docker compose up -d
env $(cat .env | xargs) ./mvnw spring-boot:run
```

Sin `--profile app`, Compose levanta solo PostgreSQL.

### Tests

```bash
./mvnw clean verify
```

Los tests de integración usan Testcontainers, así que Docker tiene que estar en marcha. El informe de cobertura queda en `target/site/jacoco/index.html`.
