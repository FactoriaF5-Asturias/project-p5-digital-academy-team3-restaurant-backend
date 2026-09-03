# Backlog Backend — Proyecto Restaurante (Team 3)

**Stack:** Java / Spring Boot + PostgreSQL
**Backend:** Ruben (PO), Iulian
**MVP1:** 16 de septiembre (2 sprints) · **Entrega final:** 5 de octubre

---

## Alcance del MVP1

Rodaja vertical mínima que demuestra producto funcionando de punta a punta:

> **Ver la carta → añadir a la cesta → confirmar pedido → la cocina lo ve y cambia su estado.**
>
> Sin autenticación: los pedidos in situ desde tablet no la requieren, y es el camino más corto a un producto demostrable.

### Decisiones de alcance (conscientes)

| Decisión | Motivo |
|---|---|
| **Sin autenticación en el MVP1** | El enunciado no exige registro para los pedidos in situ con tablet. Añadirla cuesta 10 pts de backend más días de front (guardar el token, mandarlo, gestionar el 401), y el MVP1 se demuestra igual sin ella. **Entra en el sprint 3**, con las historias B6 y B7 ya especificadas más abajo. |
| **El dominio se modela como si la auth existiera** | Contrapartida obligatoria de la decisión anterior: `Order` lleva ya su campo `customer` (nullable), las rutas se piensan por rol y los tests se escriben para que añadir un token después sea añadir una cabecera. Esto se lleva por delante el grueso del coste de retrofit. |
| **Sin WebSocket/STOMP** | El seguimiento del estado se resuelve con `GET` periódico desde el front. STOMP no suma puntos y cuesta días. |
| **Sin MapStruct** | Mapeo Entity→DTO a mano (`toResponse()`, 6 líneas). Se evita la config de procesadores de anotaciones. |
| **Sin Testcontainers (de momento)** | Tests de integración con H2 (`MODE=PostgreSQL`) o el Postgres del `docker-compose`. Testcontainers exige Docker corriendo en las 8 máquinas y configuración extra en CI, y añade 5-20 s a cada ejecución de la suite, lo que mata el ciclo de TDD. **Entra en el sprint 3** (ver más abajo). |
| **Sin Stripe / Storage / PDF / Scheduler** | Bloque de facturación e informes: sprint 3. |
| **Se mantiene `service/interfaces` + `impl`** | Ceremonia, pero la rúbrica puntúa SOLID y separación de capas. |
| **La cesta NO es backend** | Estado temporal del front (store de Pinia). El backend solo recibe el pedido confirmado. |

### Bugs a corregir en el spike (no son deuda, nadie los decidió)

- [ ] El paquete de `src/test` es `com.example.demo` y debe ser `team3.dev.restaurante` (resto de la plantilla de Spring Initializr; rompe `@SpringBootTest` y el acceso *package-private*).

### Deuda técnica registrada (decisiones conscientes, con fecha de pago)

- [ ] **Endpoints B3 y B4 sin protección por rol** — sprint 3, al entrar la auth. Hoy cualquiera puede listar pedidos y cambiarles el estado.
- [ ] **B5 no comprueba propietario** — sprint 3. Cualquiera que teclee `/api/pedidos/1` ve un pedido ajeno (**OWASP A01 — Broken Access Control**).
- [ ] **Perfil completo del cliente** — sprint 3, junto con la auth.
- [ ] **Sin refresh token** — sprint 3 o nunca, según tiempo.
- [ ] **Testcontainers** — sprint 3, antes de las consultas de agregación de ventas (ver el backlog de más abajo).

---

## Sprints

| Sprint 1 (3 → 9 sept) | Pts | | Sprint 2 (10 → 16 sept) | Pts |
|---|---|---|---|---|
| Spike: arranque del proyecto | 8 | | HU-B3 Listar pedidos cocina | 3 |
| HU-B1 Servir catálogo | 3 | | HU-B4 Actualizar estado | 5 |
| HU-B2 Crear pedido | 8 | | HU-B5 Consultar pedido | 2 |
| **Total** | **19** | | **Total** | **10** |

> El sprint 2 va deliberadamente ligero: es la semana en que todo se junta con el frontend, y esa integración siempre consume días que nadie estimó. Si vais sobrados, se adelantan las historias B6 y B7 (auth) desde el sprint 3.

**Reglas de reparto**
- Una historia, una persona (evita conflictos de merge).
- El spike de arranque se hace **entre los dos, en una sesión**, y se mergea a `main` antes de que nadie abra rama de feature.
- La velocidad del equipo no se conoce todavía: se mide al cerrar el sprint 1 y se usa ese dato para replanificar.

**Decisión de diseño que se aplica desde el sprint 1, antes de que exista la auth:** el dominio se modela como si la autenticación ya estuviera. `Order` lleva su campo `customer` (nullable en el sprint 1), las rutas se piensan por rol, y los tests de integración se escriben de forma que añadir un token después sea añadir una cabecera, no reescribirlos.

---

## SPIKE — Arranque del proyecto · 8 pts

Tarea técnica, no historia de usuario. Bloquea todo lo demás.

- [ ] `pom.xml`: Spring Web, Data JPA, Validation, PostgreSQL driver, Lombok, SpringDoc OpenAPI, Spring Security, JJWT
- [ ] `docker-compose.yml` con Postgres
- [ ] `application.yml` (perfiles `dev` / `test`) y `.env.example`
- [ ] Estructura de paquetes bajo `team3.dev.restaurante`
- [ ] **Corregir el paquete de tests** a `team3.dev.restaurante`
- [ ] `GlobalExceptionHandler` (`@RestControllerAdvice`) + excepciones propias
- [ ] Wrapper de respuesta `ApiResponse<T>`
- [ ] Configuración de CORS para el front
- [ ] Swagger accesible en `/swagger-ui.html`
- [ ] README con guía de instalación y arranque

---

## HU-B1 — Servir el catálogo de productos · 3 pts

> Como **cliente**, quiero **recibir los productos de la carta**, para **decidir mi pedido con información actualizada de precio y disponibilidad**.

### Criterios de aceptación

```
Dado que existen productos activos en la base de datos
Cuando el frontend hace GET /api/productos
Entonces recibo 200 OK y una lista en JSON donde cada producto trae
        id, nombre, descripción, precio, categoría, imagen y disponible

Dado que un producto está desactivado por falta de existencias
Cuando el frontend hace GET /api/productos
Entonces ese producto no aparece en la lista (o aparece con disponible: false)
```

### Subtareas

- [ ] Entidad `Product` (id, nombre, descripción, precio, categoría, imagen, disponible)
- [ ] Enum `ProductCategory` (ESPECIALIDAD, BEBIDA, POSTRE)
- [ ] `ProductRepository` (`JpaRepository`)
- [ ] `ProductResponse` (DTO de salida)
- [ ] Test del `ProductService`
- [ ] `ProductService` con el método que devuelve la carta
- [ ] `ProductController` con `GET /api/productos`
- [ ] Datos semilla (`data.sql`) con productos de las 3 categorías
- [ ] Test de integración del endpoint

> Endpoint **público**: la carta se ve sin estar registrado, también desde las tablets del local.

---

## HU-B2 — Recibir y registrar un pedido · 8 pts

> Como **cliente**, quiero **enviar mis platos elegidos**, para **recibir mi comida sin errores ni esperas innecesarias**.

### Criterios de aceptación

```
Dado que los productos solicitados existen y están disponibles
Cuando el frontend hace POST /api/pedidos con la lista de productos y cantidades
Entonces recibo 201 Created con el pedido creado: id, estado PENDIENTE,
        líneas del pedido y total

Dado que un producto del pedido no existe en la BD
Cuando el frontend hace POST /api/pedidos
Entonces recibo 404 Not Found y no se crea ningún pedido

Dado que una cantidad es 0 o negativa
Cuando el frontend hace POST /api/pedidos
Entonces recibo 400 Bad Request con el mensaje del campo inválido

Dado que la lista de productos viene vacía
Cuando el frontend hace POST /api/pedidos
Entonces recibo 400 Bad Request

Dado que un producto existe pero está desactivado por falta de existencias
Cuando el frontend hace POST /api/pedidos
Entonces recibo 409 Conflict
```

### Subtareas

- [ ] Enum `OrderStatus` (PENDIENTE, EN_CURSO, CON_RETRASO, LISTO)
- [ ] Enum `OrderType` (LOCAL, DOMICILIO)
- [ ] Entidad `Order` (id, fecha, estado, total, tipo, `customer` **nullable**)
- [ ] Entidad `OrderLine` (id, order, product, cantidad, precioUnitario)
- [ ] Relación `Order` 1─N `OrderLine` (`cascade` + `orphanRemoval`)
- [ ] `CreateOrderRequest` con Bean Validation (`@NotEmpty` en la lista, `@Min(1)` en cantidades)
- [ ] `OrderResponse` (id, estado, líneas, total)
- [ ] `OrderRepository`
- [ ] Test del `OrderService`: camino feliz + producto inexistente + producto no disponible
- [ ] `OrderService.create()`: valida productos, calcula el total, guarda con estado PENDIENTE
- [ ] `OrderController` con `POST /api/pedidos` → 201
- [ ] `GlobalExceptionHandler`: 404 y 409
- [ ] Test de integración (MockMvc): 201, 400, 404, 409

### Decisiones de diseño

**`precioUnitario` se guarda en la línea del pedido.** Es una foto del precio en el momento de pedir, no un enlace al producto. Si se lee de `product.precio`, al subir un precio cambian retroactivamente todos los pedidos antiguos y las facturas dejan de cuadrar. Regla general en cualquier sistema con facturación: lo que ya ocurrió no se recalcula.

**El total lo calcula el backend, nunca llega en el JSON.** Cualquiera puede interceptar la petición y mandar `total: 0.01`. El cliente manda intenciones; el servidor calcula hechos leyendo su propia BD. Crítico de cara a integrar Stripe.

**`customer` nace nullable.** Los pedidos desde tablet no tienen usuario detrás; los online sí. En el MVP1 va siempre a null porque no hay usuarios, pero el campo existe desde el sprint 1 para no migrar el esquema cuando llegue la auth.

---

## Historias de autenticación — **FUERA DEL MVP1**, planificadas para el sprint 3

Especificadas ya para no rehacer el análisis en octubre. No se implementan antes del 16 de septiembre.

## HU-B6 — Registro de cliente · 5 pts

> Como **visitante**, quiero **crear una cuenta con mi email y una contraseña**, para **hacer pedidos a domicilio y acceder a las ofertas exclusivas**.

### Criterios de aceptación

```
Dado que el email no está registrado
Cuando el frontend hace POST /api/auth/registro con email y password
Entonces recibo 201 Created con el id y el email del usuario
        y la contraseña queda guardada como hash BCrypt, nunca en claro

Dado que el email ya existe en la BD
Cuando el frontend hace POST /api/auth/registro
Entonces recibo 409 Conflict

Dado que el email no tiene formato válido o la contraseña es más corta de 8 caracteres
Cuando el frontend hace POST /api/auth/registro
Entonces recibo 400 Bad Request indicando el campo

Dado cualquier respuesta de este endpoint
Cuando se inspecciona el JSON devuelto
Entonces el campo password no aparece bajo ninguna forma
```

### Subtareas

- [ ] Enum `UserRole` (CLIENTE, COCINA, MOTORISTA, ADMIN)
- [ ] Entidad `User` (id, email único, passwordHash, rol, fechaAlta)
- [ ] `UserRepository` con `existsByEmail` y `findByEmail`
- [ ] `RegisterRequest` con `@Email` y `@Size(min = 8)`
- [ ] `UserResponse` (id, email, rol) — **sin password**
- [ ] Bean `PasswordEncoder` (BCrypt) en `config/security`
- [ ] Test del `AuthService`: alta correcta, email duplicado, hash aplicado
- [ ] `AuthService.register()`
- [ ] `AuthController` con `POST /api/auth/registro`
- [ ] Test de integración: 201, 400, 409 y ausencia de password en la respuesta

> El test de "el password no sale en el JSON" parece tonto y es el que evita el fallo nº1 de este tipo de proyectos. Escribirlo.

---

## HU-B7 — Login y emisión de token · 5 pts

> Como **usuario registrado**, quiero **identificarme con mi email y contraseña**, para **acceder a lo que me corresponde según mi rol sin volver a autenticarme en cada pantalla**.

### Criterios de aceptación

```
Dado que existe un usuario con esas credenciales
Cuando el frontend hace POST /api/auth/login
Entonces recibo 200 OK con un token JWT que contiene el email y el rol

Dado que la contraseña es incorrecta o el email no existe
Cuando el frontend hace POST /api/auth/login
Entonces recibo 401 Unauthorized con un mensaje genérico

Dado que se llama a un endpoint protegido sin token
Cuando llega la petición
Entonces recibo 401 Unauthorized

Dado que un usuario con rol CLIENTE llama a un endpoint de cocina
Cuando llega la petición con su token válido
Entonces recibo 403 Forbidden
```

### Subtareas

- [ ] `LoginRequest` y `TokenResponse`
- [ ] `JwtService`: generar y validar token (JJWT), secreto por variable de entorno
- [ ] `UserDetailsServiceImpl` sobre `UserRepository`
- [ ] `JwtAuthenticationFilter`
- [ ] `SecurityFilterChain`: público `/api/productos`, `/api/auth/**` y Swagger; el resto autenticado
- [ ] Test del `JwtService`: token válido, token caducado, firma manipulada
- [ ] `AuthController` con `POST /api/auth/login`
- [ ] Test de integración: 200, 401 sin token, 401 con credenciales malas, 403 con rol equivocado

### Decisiones de diseño

**El mensaje del 401 es genérico** ("credenciales incorrectas"), nunca "ese email no existe". Distinguir los dos casos permite averiguar qué emails están registrados — es *user enumeration*, y es exactamente lo que el profe busca cuando puntúa seguridad.

**401 vs 403:** 401 es "no sé quién eres" (falta token o es inválido). 403 es "sé quién eres y no te toca". Confundirlos es el error clásico.

**El secreto del JWT va en variable de entorno**, nunca en `application.yml` subido al repo.

---

## HU-B3 — Listar los pedidos pendientes para cocina · 3 pts

> Como **cocinero**, quiero **obtener los pedidos activos ordenados por hora de entrada**, para **preparar los platos en el orden correcto y no dejar a nadie esperando**.

### Criterios de aceptación

```
Dado que existen pedidos en estado PENDIENTE y EN_CURSO
Cuando el frontend hace GET /api/pedidos?estado=activos
Entonces recibo 200 OK con la lista ordenada por fecha ascendente,
        y cada pedido trae id, hora, estado, líneas (producto + cantidad) y total

Dado que no hay ningún pedido activo
Cuando el frontend hace GET /api/pedidos?estado=activos
Entonces recibo 200 OK y una lista vacía (nunca 404)
```

### Subtareas

- [ ] `findByEstadoInOrderByFechaAsc` en `OrderRepository`
- [ ] `OrderService.listActive()`
- [ ] `GET /api/pedidos` con parámetro `?estado=`
- [ ] `OrderSummaryResponse` (versión ligera, sin datos del cliente)
- [ ] Test del service (con datos y con lista vacía)
- [ ] Test de integración: 200 y lista vacía

> **Deuda:** endpoint abierto en el MVP1. Al entrar la auth (sprint 3) se añade `@PreAuthorize` de rol COCINA/ADMIN y su criterio de 403.

> Una colección vacía **no es un 404**: la colección existe, está vacía. El 404 se reserva para un recurso concreto que no existe (`/api/pedidos/999`).

---

## HU-B4 — Actualizar el estado de un pedido · 5 pts

> Como **cocinero**, quiero **cambiar el estado de un pedido a en curso, con retraso o listo**, para **que el cliente sepa en todo momento cómo va lo suyo sin tener que preguntar**.

### Criterios de aceptación

```
Dado que existe un pedido en estado PENDIENTE
Cuando el frontend hace PATCH /api/pedidos/{id}/estado con {"estado": "EN_CURSO"}
Entonces recibo 200 OK con el pedido actualizado

Dado que el id no corresponde a ningún pedido
Cuando se hace PATCH /api/pedidos/999/estado
Entonces recibo 404 Not Found

Dado que el estado enviado no existe en OrderStatus
Cuando se hace PATCH /api/pedidos/{id}/estado con {"estado": "QUEMADO"}
Entonces recibo 400 Bad Request

Dado que el pedido ya está en estado LISTO
Cuando se intenta devolverlo a PENDIENTE
Entonces recibo 409 Conflict
```

### Subtareas

- [ ] `UpdateStatusRequest` con el enum validado
- [ ] `OrderService.updateStatus(id, estado)`
- [ ] Regla de transiciones válidas: `PENDIENTE → EN_CURSO → LISTO`; `CON_RETRASO` desde cualquiera salvo `LISTO`
- [ ] `PATCH /api/pedidos/{id}/estado`
- [ ] Test del service: transición válida, id inexistente, transición prohibida
- [ ] Test de integración: 200, 400, 404, 409

> **Deuda:** endpoint abierto en el MVP1. Sprint 3: `@PreAuthorize` de rol COCINA/ADMIN y criterio de 403.

### Decisiones de diseño

**`PATCH` y no `PUT`:** `PUT` sustituye el recurso entero; `PATCH` modifica un campo. La ruta acaba en `/estado` para dejar explícito que es lo único que ese endpoint puede tocar — no se puede colar un precio nuevo por ahí.

**La regla de transiciones es una máquina de estados**, y es lógica de negocio que vive en el service (por eso esta historia son 5 puntos y no 3). Sin ella, un `PATCH` mal formado devuelve a "pendiente" un pedido ya servido.

---

## HU-B5 — Consultar el estado de un pedido · 2 pts

> Como **cliente**, quiero **consultar en qué punto está mi pedido**, para **saber cuánto me queda sin tener que llamar al restaurante**.

### Criterios de aceptación

```
Dado que existe un pedido con id 42
Cuando el frontend hace GET /api/pedidos/42
Entonces recibo 200 OK con id, estado, líneas, total y hora de creación

Dado que no existe ningún pedido con ese id
Cuando se hace GET /api/pedidos/999
Entonces recibo 404 Not Found con un mensaje claro
```

### Subtareas

- [ ] `OrderService.findById(id)` con `ResourceNotFoundException`
- [ ] `GET /api/pedidos/{id}`
- [ ] Reutilizar `OrderResponse`
- [ ] Test del service: encontrado / no encontrado
- [ ] Test de integración: 200 y 404

> **Deuda consciente:** en el MVP1 cualquiera que teclee `/api/pedidos/1` ve un pedido ajeno. Es **OWASP A01 — Broken Access Control**, el fallo nº1 de la lista. Se paga en el sprint 3 comprobando que el pedido pertenece al usuario autenticado. Que aparezca escrito y planificado en la documentación suma; que lo encuentre el profe en la demo, resta.

---

## Resumen de endpoints del MVP1

| Método | Ruta | Historia | Acceso | Respuestas |
|---|---|---|---|---|
| `GET` | `/api/productos` | B1 | público | 200 |
| `POST` | `/api/pedidos` | B2 | público | 201, 400, 404, 409 |
| `GET` | `/api/pedidos?estado=activos` | B3 | público (deuda) | 200 |
| `GET` | `/api/pedidos/{id}` | B5 | público (deuda) | 200, 404 |
| `PATCH` | `/api/pedidos/{id}/estado` | B4 | público (deuda) | 200, 400, 404, 409 |

Al entrar la auth en el sprint 3, la columna de acceso pasa a: `/api/auth/**` público, B3 y B4 restringidos a COCINA/ADMIN, y B5 al propietario del pedido.

**Modelo de datos del MVP1:** `Product`, `Order`, `OrderLine` + enums `ProductCategory`, `OrderStatus`, `OrderType`.

`Order.customer` existe desde el sprint 1 como campo nullable, para no migrar el esquema cuando llegue `User`.

---

## Backlog posterior al MVP1 (sprint 3, hasta el 5 de octubre)

Sin estimar todavía. Pendiente de conocer la velocidad real del equipo tras el sprint 1.

- **HU-B6 Registro de cliente** (5 pts) y **HU-B7 Login y emisión de token** (5 pts) — ya especificadas más arriba
- Proteger B3 y B4 por rol, y B5 por propietario
- Perfil completo del cliente (nombre, apellidos, dirección, CP, ciudad) y su validación
- Dashboard de motoristas: en tránsito / entregado
- Email de notificación al marcar en tránsito
- CRUD de productos para administración (alta, desactivar, suprimir)
- Pagos con Stripe · estado PAGADO · tabla de facturación
- Totales de ventas: diario, mensual, trimestral, anual
- **Migrar los tests de integración a Testcontainers** — obligatorio antes o a la vez que las consultas de agregación de ventas: H2 y Postgres divergen en `date_trunc`, tipos y funciones de fecha, y a partir de ahí un test verde en H2 no demuestra nada. Para que la migración cueste una tarde y no una semana: (1) toda la config de BD de test aislada en `application-test.yml`, (2) migraciones escritas en Postgres puro, sin funciones propias de H2.
- Generación automática de resumen de ventas en PDF (`@Scheduled`)
- Subida del PDF a almacenamiento de objetos (Firebase / Supabase)
- Ofertas exclusivas para clientes registrados
- Refresh token (si sobra tiempo)
