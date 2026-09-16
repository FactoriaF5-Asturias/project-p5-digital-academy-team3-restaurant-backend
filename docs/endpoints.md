# Endpoints

Aqui vamos a representar todas las rutas endpoint

## Formato de tabla:

Method - Endpoint - Envia - Recibe

| Hecho | Method | Endpoint                      | Da                                                                 | Recibe                                       | Errores       |
| ----- | ------ | ----------------------------- | ------------------------------------------------------------------ | -------------------------------------------- | ------------- |
| [X]   | GET    | `/api/products`               | ---                                                                | Lista de productos activos                   | 500           |
| [X]   | GET    | `/api/products/{id}`          | `id` del producto                                                  | Producto                                     | 404, 400      |
| [X]   | POST   | `/api/products`               | `name`, `description`, `categoryId`, `price`, `imageURL`, `status` | Producto creado                              | 400, 404, 409                 |
| [X]   | PUT    | `/api/products/{id}`          | `id` + datos del producto a modificar                              | Producto actualizado                         | 400, 404, 409                 |
| [X]   | DELETE | `/api/products/{id}`          | `id` del producto                                                  | 204 sin contenido (soft delete: `status` a false) | 404                      |
| [X]   | GET    | `/api/categories`             | ---                                                                | Lista de categorías                          | 500           |
| [X]   | GET    | `/api/categories/{id}`        | `id` de la categoría                                               | Categoría (`id` + `name`)                    | 404                           |
| [X]   | POST   | `/api/categories`             | `name`                                                             | Categoría creada                             | 400           |
| [X]   | PUT    | `/api/categories/{id}`        | `id` + `name`                                                      | Categoría actualizada                        | 404, 400, 409 |
| [X]   | DELETE | `/api/categories/{id}`        | `id` de la categoría                                               | Confirmación de eliminación                  | 404, 409      |
| [X]   | GET    | `/api/tablets`                | ---                                                                | Lista de mesas/tablets en restaurante        |               |
| [X]   | GET    | `/api/tablets/{id}`           | `id` de la tablet                                                  | Tablet                                       |               |
| [X]   | GET    | `/api/payment-methods`        | ---                                                                | Lista de métodos de pago (`CASH`, `CARD`)    |               |
| [ ]   | POST   | `/api/payments/create-intent` | `amount`, `currency`, `paymentMethod`                              | `clientSecret` + `paymentIntentId` de Stripe |               |
| [X]   | POST   | `/api/orders`                 | `tabletId`, `payment_method_name`, `order_type_name`, `items[]`    | Crear `ORDER` + `ORDER_ITEMS`    |               |
| [X]   | GET    | `/api/orders`                 | ---                                                                | Visualizar lista de Orders                   |               |
| [X]   | GET    | `/api/orders/{id}`            | `id` del pedido                                                    | Datos completos del pedido                   |               |
| [ ]   | PATCH  | `/api/orders/{id}/status`     | `id` del pedido + `status`                                         | Pedido con el nuevo estado                   |               |
| [X]   | GET    | `/api/orders/{id}/invoice`    | `id` del pedido                                                    | Datos de la factura en JSON                      |               |
| [X]   | GET    | `/api/invoices`              | ---                                                                | Lista de facturas en JSON                    | 500           |
| [ ]   | WS     | `/ws/orders/{id}`             | `id` del pedido                                                    | Actualizaciones del estado en tiempo real    |               |

## Probar peticiones de endpoints

### Probar peticiones de pedidos

#### POST /api/v1/orders

Para probar conexion y peticion a `POST /api/v1/orders`

1. Introducir este comando en terminal bash

```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.jvmArguments="-DDATABASE_USERNAME=postgres -DDATABASE_PASSWORD=postgres"
```
2. Comprobar que el header de peticion contiene:

Content-Type: application/json

3. Enviar el body con los siguientes datos(ESTO ES UN EJEMPLO):


```
{
  "tabletId": 2,
  "orderTypeName": "DINE IN",
  "paymentMethodName": "CASH",
  "items": [
    {
      "productId": 4,
      "quantity": 2
    },
    {
      "productId": 7,
      "quantity": 1
    }
  ]
}
```
4. Respuesta tiene quer ser: 201 ok, significa que el order ha sido creado. Para verlo usar otro endpoint GET api/v1/orders o GET /api/orders/{id}.

### Probar peticiones de facturas

Las rutas actuales usan el prefijo `/api/v1`. Estos endpoints devuelven datos
JSON, no PDF. Para probar sin autenticación la factura de un pedido, arrancar
con el perfil `local`.

#### GET /api/invoices

Para probar conexión y petición a `GET /api/v1/invoices`:

1. Introducir este comando en terminal bash. Este ejemplo usa `postgres` como
   usuario y contraseña; sustituirlos si tus credenciales son diferentes.

```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.jvmArguments="-DDATABASE_USERNAME=postgres -DDATABASE_PASSWORD=postgres"
```

2. Esperar a que aparezca `Started GiacobelloApplication`. En Postman seleccionar
   método **GET**, **No Auth** y esta URL:

```text
http://localhost:8080/api/v1/invoices
```

3. No enviar body. También se puede probar desde otra terminal:

```bash
curl -i http://localhost:8080/api/v1/invoices
```

4. La respuesta tiene que ser **200 OK** con una lista de facturas.
   Estos datos son un ejemplo:

```json
[
  {
    "id": 3,
    "orderId": 5,
    "invoiceNumber": "TEST-5",
    "totalAmount": 25.00,
    "issuedAt": "2026-09-16T12:00:00"
  }
]
```

Si no hay facturas, devuelve **200 OK** con `[]`. El listado actualmente permite
acceso sin autenticación también sin el perfil `local`.

#### GET /api/orders/{id}/invoice

Para probar conexión y petición a `GET /api/v1/orders/{id}/invoice`:

1. Introducir este comando en terminal bash, si el backend no está ya arrancado
   con el perfil `local`:

```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.jvmArguments="-DDATABASE_USERNAME=postgres -DDATABASE_PASSWORD=postgres"
```

2. Consultar primero `GET /api/v1/invoices` y copiar el **orderId** de una factura.
   En Postman seleccionar método **GET**, **No Auth** y sustituir `5` por ese ID:

```text
http://localhost:8080/api/v1/orders/5/invoice
```

3. No enviar body. También se puede probar desde otra terminal:

```bash
curl -i http://localhost:8080/api/v1/orders/5/invoice
```

4. La respuesta tiene que ser **200 OK** con `[]` si existe la factura o **404** si no existe(de momento es asi y es correcto ya que no tenemos facturas). Estos datos son un ejemplo:

```json
{
  "id": 3,
  "orderId": 5,
  "invoiceNumber": "TEST-5",
  "totalAmount": 25.00,
  "issuedAt": "2026-09-16T12:00:00"
}
```

La URL usa el ID del pedido (`5`), no el ID de la factura (`3`).
Si el pedido no existe o no tiene factura, devuelve **404 Not Found**.
Al crear un pedido válido mediante `POST /api/v1/orders`, el backend crea de
forma atómica la factura asociada en `invoices`, con el total del pedido y su
fecha de emisión. Por tanto, el `orderId` devuelto por el POST se puede usar
directamente en este endpoint.

5. Si aparece **403**, comprobar que el perfil `local` está activo y la ruta está
   permitida en `SecurityConfig`. Si aparece **404** en el listado, revisar la URL
   y reiniciar el backend después de los cambios. Si no conecta, comprobar que
   el backend está arrancado en el puerto `8080`.
## Notas

- **`createdAt` de los pedidos** se devuelve en formato ISO-8601, por ejemplo
  `2026-09-16T22:44:26`, tanto para `GET /api/v1/orders` como para
  `GET /api/v1/orders/{id}`.
- **Borrado físico de pedidos mediante SQL**: las migraciones `V19` y `V20` configuran
  las FK de `order_items.order_id` e `invoices.order_id` con `ON DELETE CASCADE`.
  Por tanto, al ejecutar `DELETE FROM orders WHERE id = <id>;` se eliminan también
  sus líneas y su factura. Los productos asociados no se eliminan. Actualmente no
  existe un endpoint HTTP `DELETE /api/v1/orders/{id}`.
- **`DELETE /api/products/{id}` no borra la fila**: pone `status` a `false` (soft delete). La FK
  `order_items.product_id` impide borrar un producto que ya aparece en algún pedido, y borrarlo
  rompería pedidos y facturas ya emitidas. Por eso el DELETE no devuelve 409: siempre puede
  desactivar.
- **`GET /api/products` solo devuelve productos activos** (`status = true`), en coherencia con lo
  anterior.
