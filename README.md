# BACKEND RESTAURANTE

## Planificación

Antes de picar código, necesito tener preparado:
- [X] Dependencias. ¿Qué necesito? 
- [ ] Tables para la DB y documentación de campos.
- [ ] Diagrama Chen y patas de gallo.
- [X] Lógica de negocio ¿Workflow de cesta? ¿Flujo necesario para MvP?.
- [ ] Estructura de proyecto.

## Tree-Project

```
backend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── team3
│   │   │       └── dev
│   │   │           └── restaurante
│   │   │               │
│   │   │               ├── config
│   │   │               │   ├── AppContainer.java   // Arrancador del proyecto
│   │   │               │   ├── security        // SecurityFilterChain, CORS, PasswordEncoder bean
│   │   │               │   ├── openapi          // SpringDoc/Swagger config
│   │   │               │   ├── websocket        // configuración de canales STOMP/WebSocket
│   │   │               │   ├── storage           // config del cliente S3/Firebase
│   │   │               │   └── mail              // config de JavaMailSender
│   │   │               │
│   │   │               ├── security
│   │   │               │   ├── filter            // JwtAuthenticationFilter, etc. (Filtros de Seguridad)
│   │   │               │   ├── jwt                // generación/validación de tokens (JJWT)
│   │   │               │   └── userdetails        // UserDetailsService, UserPrincipal
│   │   │               │
│   │   │               ├── controller
│   │   │               │   ├── auth
│   │   │               │   ├── user
│   │   │               │   ├── product
│   │   │               │   ├── category
│   │   │               │   ├── cart
│   │   │               │   ├── order
│   │   │               │   ├── payment
│   │   │               │   ├── delivery
│   │   │               │   ├── invoice
│   │   │               │   ├── offer
│   │   │               │   └── salesreport
│   │   │               │
│   │   │               ├── service
│   │   │               │   ├── interfaces         // interfaces globales (contratos de cada servicio)
│   │   │               │   └── impl               // implementaciones concretas (inyectadas por interfaz)
│   │   │               │
│   │   │               ├── repository
│   │   │               │   // interfaces JpaRepository por entidad (UserRepository, OrderRepository...)
│   │   │               │
│   │   │               ├── entity
│   │   │               │   // clases JPA mapeadas 1:1 con las tablas de PostgreSQL
│   │   │               │
│   │   │               ├── dto
│   │   │               │   ├── request            // DTOs de entrada (CreateOrderRequest, LoginRequest...)
│   │   │               │   └── response           // DTOs de salida (OrderResponse, ProductResponse...)
│   │   │               │
│   │   │               ├── mapper
│   │   │               │   // interfaces @Mapper de MapStruct (Entity <-> DTO)
│   │   │               │
│   │   │               ├── enums
│   │   │               │   // OrderStatus, OrderType, PaymentMethod, PaymentStatus, ProductStatus, UserRole...
│   │   │               │
│   │   │               ├── global
│   │   │               │   ├── exception
│   │   │               │   │   ├── handler        // @ControllerAdvice / GlobalExceptionHandler
│   │   │               │   │   └── custom         // excepciones propias (ResourceNotFoundException...)
│   │   │               │   ├── response            // wrapper genérico de respuesta (ApiResponse<T>)
│   │   │               │   └── constants           // constantes compartidas de la app
│   │   │               │
│   │   │               ├── validation
│   │   │               │   // validadores custom (@Constraint) si hacen falta más allá de Bean Validation
│   │   │               │
│   │   │               ├── external
│   │   │               │   ├── stripe             // integración con Stripe SDK
│   │   │               │   ├── storage            // cliente de subida a Supabase/Firebase Storage
│   │   │               │   └── pdf                // generación de PDFs (resumen ventas, facturas)
│   │   │               │
│   │   │               ├── scheduler
│   │   │               │   // tareas @Scheduled (generación automática de SALES_REPORT)
│   │   │               │
│   │   │               ├── websocket
│   │   │               │   // handlers/controllers STOMP para tracking en tiempo real
│   │   │               │
│   │   │               └── util
│   │   │                   // helpers genéricos (fechas, formateo, etc.)
│   │   │
│   │   └── resources
│   │       ├── db
│   │       │   └── migration      // scripts Flyway (V1__init.sql, V2__...)
│   │       ├── static
│   │       ├── templates          // plantillas de email (si usáis Thymeleaf para el mail)
│   │       └── application.yml
│   │
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── demo
│                       ├── controller
│                       ├── service
│                       ├── repository
│                       └── integration        // tests con Testcontainers
│
├── docker
│   └── docker-compose.yml         // Postgres local (+ MailHog opcional)
│
├── .env / .env.example
├── pom.xml
└── README.md
```

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
