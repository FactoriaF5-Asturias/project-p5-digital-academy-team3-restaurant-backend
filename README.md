# BACKEND GIACOBELLO

## Planificación

Antes de picar código, necesito tener preparado:
- [X] Dependencias. ¿Qué necesito? 
- [X] Tables para la DB y documentación de campos.
- [ ] Diagrama Chen y patas de gallo.
- [X] Lógica de negocio ¿Workflow de cesta? ¿Flujo necesario para MvP?.
- [ ] Estructura de proyecto.

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

## Dependencias
### Testing

- **Spring Boot Test**: aporta la base de utilidades para test unitarios e integración.
- **JUnit 5**: framework de ejecución y aserciones de los tests.
- **Mockito**: simula dependencias externas para aislar la lógica en tests unitarios.
- **MockMvc**: prueba los controllers simulando peticiones HTTP sin levantar servidor real.
- **Testcontainers**: levanta un PostgreSQL real en Docker para tests de integración fiables.
- **JaCoCo**: mide el porcentaje de cobertura de tests del proyecto.

### Ficheros

- **Firebase Storage**: almacena en la nube los PDFs de resumen de ventas y las imágenes de producto.
- **PDF library**: genera los PDFs de facturas y resúmenes de ventas.

### Limpieza de código

- **Lombok**: elimina el boilerplate de getters/setters/constructores en entidades y DTOs.
- **MapStruct**: mapea automáticamente entre entidades JPA y DTOs de la API.

### Núcleo del backend (API, persistencia, seguridad)

- **Spring Web**: expone la API REST que consumen tablets, app cliente y dashboards.
- **Spring Data JPA**: capa de persistencia hacia PostgreSQL sin SQL repetitivo.
- **PostgreSQL Driver**: conector JDBC necesario para conectar con la base de datos.
- **Spring Validation**: valida los DTOs de entrada (perfil, pedidos) antes de llegar al service.
- **Spring Security**: filtra y protege los endpoints según el rol del usuario.
- **JJWT**: firma y genera los tokens de sesión en el login.
- **OAuth2 Resource Server**: valida esos tokens JWT en cada petición autenticada.
- **Flyway**: versiona y migra el esquema de la base de datos de forma controlada.

### Integraciones externas / funcionalidades específicas

- **SpringDoc OpenAPI**: genera documentación interactiva de la API automáticamente.
- **WebSocket**: permite actualizaciones en tiempo real del estado del pedido (tracking).
- **Stripe SDK**: procesa los pagos con tarjeta de crédito/débito.
- **Email provider**: envía la notificación al cliente cuando el pedido pasa a "en tránsito".

## Documentación API

http://localhost:8080/swagger-ui/index.html

**JSON**
http://localhost:8080/v3/api-docs

**NOTA**: Para visualizar estos datos el servicio backend debe estar activo.