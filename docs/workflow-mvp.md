# MVP

## Flujo de trabajo

Se ha decidido realizar el flujo de usuario completo desde menu hasta realizar la compra de un pedido en efectivo ON_SITE

**Workflow**: Frontend > Seguridad > Controller > Service > Repository > DB

### TABLAS OBLIGATORIAS PARA MVP
- USERS: Definir clientes, cocina, staff
- PRODUCTS: catálogo de producto
- CATEGORIES: Definir tipo de productos
- TABLES: Mesas disponibles en el restaurante
- TABLETS: Tablet de la mesa donde se realizan pedidos
- ORDERS: Pedido hecho por cliente ¿Se puede prescindir de items, items_option y status_history?
- INVOICES: Factura de pedido

Atajos: 
- Delegar carrito a frontend
- ORDERS: Hacer exclusivamente la lógica para ON_SITE



