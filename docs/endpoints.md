# Endpoints

Aqui vamos a representar todas las rutas endpoint 

## Formato de tabla:

Method - Endpoint - Envia - Recibe

| Hecho | Method | Endpoint                      | Da                                                                 | Recibe                                       | Errores |
| ----- | ------ | ----------------------------- | ------------------------------------------------------------------ | -------------------------------------------- | ----------------------------- |
| [ ]   | GET    | `/api/products`               | ---                                                                | Lista de productos activos                   | 500                           |
| [ ]   | GET    | `/api/products/{id}`          | `id` del producto                                                  | Producto                                     | 404, 400                      |
| [ ]   | POST   | `/api/products`               | `name`, `description`, `categoryId`, `price`, `imageURL`, `status` | Producto creado                              | 400, 404, 409                 |
| [ ]   | PUT    | `/api/products/{id}`          | `id` + datos del producto a modificar                              | Producto actualizado                         | 400, 404, 409                 |
| [ ]   | DELETE | `/api/products/{id}`          | `id` del producto                                                  | Confirmación de eliminación                  | 404, 409                      |
| [ ]   | GET    | `/api/categories`             | ---                                                                | Lista de categorías                          | 500                           |
| [ ]   | GET    | `/api/categories/{id}`        | `id` de la categoría                                               | Categoría + productos asociados              | 404                           |
| [ ]   | POST   | `/api/categories`             | `name`                                                             | Categoría creada                             | 400                           |
| [ ]   | PUT    | `/api/categories/{id}`        | `id` + `name`                                                      | Categoría actualizada                        | 404, 400, 409                 |
| [ ]   | DELETE | `/api/categories/{id}`        | `id` de la categoría                                               | Confirmación de eliminación                  | 404, 409                      |
| [ ]   | GET    | `/api/tablets`                | ---                                                                | Lista de mesas/tablets en restaurante        |  |
| [ ]   | GET    | `/api/tablets/{id}`           | `id` de la tablet                                                  | Tablet                                       |  |
| [X]   | GET    | `/api/payment-methods`        | ---                                                                | Lista de métodos de pago (`CASH`, `CARD`)    |  |
| [ ]   | POST   | `/api/payments/create-intent` | `amount`, `currency`, `paymentMethod`                              | `clientSecret` + `paymentIntentId` de Stripe |  |
| [ ]   | POST   | `/api/orders`                 | `tabletId`, `paymentIntentId`, `paymentMethod`, `items[]`          | Crear `ORDER` + `ORDER_ITEMS` + `INVOICE`    |  |
| [ ]   | GET    | `/api/orders/{id}`            | `id` del pedido                                                    | Datos completos del pedido                   |  |
| [ ]   | PATCH  | `/api/orders/{id}/status`     | `id` del pedido + `status`                                         | Pedido con el nuevo estado                   |  |
| [ ]   | GET    | `/api/orders/{id}/invoice`    | `id` del pedido                                                    | PDF/datos de la factura                      |  |
| [ ]   | WS     | `/ws/orders/{id}`             | `id` del pedido                                                    | Actualizaciones del estado en tiempo real    |  |