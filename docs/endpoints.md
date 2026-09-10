# Endpoints

Aqui vamos a representar todas las rutas endpoint 

## Formato de tabla:

Method - Endpoint - Envia - Recibe

| Hecho | Method | Endpoint                      | Da                                                                 | Recibe                                       |
| ----- | ------ | ----------------------------- | ------------------------------------------------------------------ | -------------------------------------------- |
| [ ]   | GET    | `/api/products`               | ---                                                                | Lista de productos activos                   |
| [ ]   | GET    | `/api/products/{id}`          | `id` del producto                                                  | Producto                                     |
| [ ]   | POST   | `/api/products`               | `name`, `description`, `categoryId`, `price`, `imageURL`, `status` | Producto creado                              |
| [ ]   | PUT    | `/api/products/{id}`          | `id` + datos del producto a modificar                              | Producto actualizado                         |
| [ ]   | DELETE | `/api/products/{id}`          | `id` del producto                                                  | Confirmación de eliminación                  |
| [ ]   | GET    | `/api/categories`             | ---                                                                | Lista de categorías                          |
| [ ]   | GET    | `/api/categories/{id}`        | `id` de la categoría                                               | Categoría + productos asociados              |
| [ ]   | POST   | `/api/categories`             | `name`                                                             | Categoría creada                             |
| [ ]   | PUT    | `/api/categories/{id}`        | `id` + `name`                                                      | Categoría actualizada                        |
| [ ]   | DELETE | `/api/categories/{id}`        | `id` de la categoría                                               | Confirmación de eliminación                  |
| [ ]   | GET    | `/api/tablets`                | ---                                                                | Lista de mesas/tablets en restaurante        |
| [ ]   | GET    | `/api/tablets/{id}`           | `id` de la tablet                                                  | Tablet                                       |
| [ ]   | GET    | `/api/payment-methods`        | ---                                                                | Lista de métodos de pago (`CASH`, `CARD`)    |
| [ ]   | POST   | `/api/payments/create-intent` | `amount`, `currency`, `paymentMethod`                              | `clientSecret` + `paymentIntentId` de Stripe |
| [ ]   | POST   | `/api/orders`                 | `tabletId`, `paymentIntentId`, `paymentMethod`, `items[]`          | Crear `ORDER` + `ORDER_ITEMS` + `INVOICE`    |
| [ ]   | GET    | `/api/orders/{id}`            | `id` del pedido                                                    | Datos completos del pedido                   |
| [ ]   | PATCH  | `/api/orders/{id}/status`     | `id` del pedido + `status`                                         | Pedido con el nuevo estado                   |
| [ ]   | GET    | `/api/orders/{id}/invoice`    | `id` del pedido                                                    | PDF/datos de la factura                      |
| [ ]   | WS     | `/ws/orders/{id}`             | `id` del pedido                                                    | Actualizaciones del estado en tiempo real    |

