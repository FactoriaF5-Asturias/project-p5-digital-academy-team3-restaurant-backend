# Endpoints

Aqui vamos a representar todas las rutas endpoint

## Formato de tabla:

Method - Endpoint - Envia - Recibe

| Hecho | Method | Endpoint                      | Da                                                                 | Recibe                                       | Errores       |
| ----- | ------ | ----------------------------- | ------------------------------------------------------------------ | -------------------------------------------- | ------------- |
| [X]   | GET    | `/api/products`               | ---                                                                | Lista de productos activos                   | 500           |
| [X]   | GET    | `/api/products/{id}`          | `id` del producto                                                  | Producto                                     | 404, 400      |
| [ ]   | POST   | `/api/products`               | `name`, `description`, `categoryId`, `price`, `imageURL`, `status` | Producto creado                              | 400, 404, 409 |
| [ ]   | PUT    | `/api/products/{id}`          | `id` + datos del producto a modificar                              | Producto actualizado                         | 400, 404, 409 |
| [ ]   | DELETE | `/api/products/{id}`          | `id` del producto                                                  | Confirmación de eliminación                  | 404, 409      |
| [X]   | GET    | `/api/categories`             | ---                                                                | Lista de categorías                          | 500           |
| [ ]   | GET    | `/api/categories/{id}`        | `id` de la categoría                                               | Categoría + productos asociados              | 404           |
| [X]   | POST   | `/api/categories`             | `name`                                                             | Categoría creada                             | 400           |
| [X]   | PUT    | `/api/categories/{id}`        | `id` + `name`                                                      | Categoría actualizada                        | 404, 400, 409 |
| [X]   | DELETE | `/api/categories/{id}`        | `id` de la categoría                                               | Confirmación de eliminación                  | 404, 409      |
| [X]   | GET    | `/api/tablets`                | ---                                                                | Lista de mesas/tablets en restaurante        |               |
| [X]   | GET    | `/api/tablets/{id}`           | `id` de la tablet                                                  | Tablet                                       |               |
| [X]   | GET    | `/api/payment-methods`        | ---                                                                | Lista de métodos de pago (`CASH`, `CARD`)    |               |
| [ ]   | POST   | `/api/payments/create-intent` | `amount`, `currency`, `paymentMethod`                              | `clientSecret` + `paymentIntentId` de Stripe |               |
| [X]   | POST   | `/api/orders`                 | `tabletId`, `payment_method_name`, `order_type_name`, `items[]`    | Crear `ORDER` + `ORDER_ITEMS` + `INVOICE`    |               |
| [X]   | GET    | `/api/orders`                 | ---                                                                | Visualizar lista de Orders                   |               |
| [X]   | GET    | `/api/orders/{id}`            | `id` del pedido                                                    | Datos completos del pedido                   |               |
| [ ]   | PATCH  | `/api/orders/{id}/status`     | `id` del pedido + `status`                                         | Pedido con el nuevo estado                   |               |
| [ ]   | GET    | `/api/orders/{id}/invoice`    | `id` del pedido                                                    | PDF/datos de la factura                      |               |
| [ ]   | WS     | `/ws/orders/{id}`             | `id` del pedido                                                    | Actualizaciones del estado en tiempo real    |               |

### Probar peticiones de endpoints

Para probar conexion y peticion a POST /api/orders`

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
