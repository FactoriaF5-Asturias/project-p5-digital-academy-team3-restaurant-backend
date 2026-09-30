# BACKEND GIACOBELLO

## Planificación

Antes de picar código, necesito tener preparado:
- [X] Dependencias. ¿Qué necesito? 
- [X] Tables para la DB y documentación de campos.
- [ ] Diagrama Chen y patas de gallo.
- [X] Lógica de negocio ¿Workflow de cesta? ¿Flujo necesario para MvP?.
- [ ] Estructura de proyecto.

## Dependencias
Este inventario recoge las dependencias y herramientas declaradas en el proyecto.

### Testing

- **Spring Boot Test**: aporta utilidades generales para tests unitarios e integración, incluidos JUnit 5 y Mockito.
- **Spring Boot WebMVC Test**: aporta utilidades de pruebas MVC, incluido MockMvc.
- **Testcontainers**: integra los tests con contenedores PostgreSQL.

### Ficheros

- **Firebase Admin SDK**: SDK administrativo de Firebase. El proyecto no incluye una dependencia específica de Firebase Storage.
- **Apache PDFBox**: biblioteca para trabajar con documentos PDF.

### Limpieza de código

- **Lombok**: reduce código repetitivo y también está configurado como procesador de anotaciones.
- **MapStruct**: genera mapeadores entre entidades y DTOs. El compilador configura sus procesadores de anotaciones junto con Lombok.

### Núcleo del backend (API, persistencia, seguridad)

- **Spring Boot**: base de la aplicación.
- **Spring Web**: expone la API REST que consumen tablets, app cliente y dashboards.
- **Spring Data JPA**: capa de persistencia hacia PostgreSQL.
- **PostgreSQL Driver**: conector necesario para conectar con la base de datos.
- **Spring Validation**: valida los DTOs de entrada antes de llegar al servicio.
- **Spring Security**: protege los endpoints según el rol del usuario.
- **OAuth2 Resource Server**: da soporte a la autenticación con JWT. La aplicación usa `JwtEncoder`/`JwtDecoder` de Spring Security con Nimbus.
- **JJWT**: también está declarado, aunque el código Java actual usa la API JWT de Spring Security y no referencia JJWT.
- **Flyway**: versiona y migra el esquema PostgreSQL de forma controlada.

### Integraciones externas / funcionalidades específicas

- **SpringDoc OpenAPI**: genera documentación interactiva de la API automáticamente.
- **WebSocket**: proporciona soporte para comunicación en tiempo real.
- **Stripe SDK**: permite integrar pagos con tarjeta.
- **Spring Boot Mail**: proporciona soporte para el envío de correo.

Las dependencias de integración mencionadas arriba están declaradas en el proyecto, pero no se han encontrado referencias a ellas en el código Java actual; por tanto, su presencia no implica que esas funcionalidades estén ya implementadas.

### Herramientas de compilación

- **Spring Boot Maven Plugin**: tareas de empaquetado y ejecución de la aplicación.
- **Maven Compiler Plugin**: compilación Java y configuración de procesadores de anotaciones.
- **JaCoCo Maven Plugin**: instrumenta los tests y genera el informe de cobertura.

## Documentación API

http://localhost:8080/swagger-ui/index.html

**JSON**
http://localhost:8080/v3/api-docs

**NOTA**: Para visualizar estos datos el servicio backend debe estar activo.

## Autenticación local

El login acepta `POST /api/v1/auth/token` con un JSON `{"username":"...","password":"..."}` y devuelve `{"token":"..."}`. Configura `JWT_KEY` en el entorno antes de iniciar el backend; debe contener al menos 64 bytes UTF-8 para HS512. No guardes este valor en el repositorio. En PowerShell puedes generar uno para la sesión actual con:

```powershell
$env:JWT_KEY = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```