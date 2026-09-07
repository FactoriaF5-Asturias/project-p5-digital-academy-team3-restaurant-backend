# MVP

## Flujo de trabajo

Se ha decidido realizar el flujo de usuario completo desde menu hasta realizar la compra de un pedido en efectivo ON_SITE

**Workflow**: Frontend > Seguridad > Controller > Service > Repository > DB

### TABLAS OBLIGATORIAS PARA MVP
- PRODUCTS: catálogo de producto
- CATEGORIES: Definir tipo de productos
- TABLETS: Tablet de la mesa donde se realizan pedidos
- ORDERS: Pedido hecho por cliente
- ORDER_ITEMS: Productos, cantidades y precio de cada producto incluido en un pedido.
- INVOICES: Factura de pedido

Atajos: 
- Delegar carrito a frontend - Al confirmar el pedido y realizar el pago, se crea la ORDER junto con sus ORDER_ITEMS y su INVOICE.
- ORDERS: Hacer exclusivamente la lógica para ON_SITE

**NOTA**: Una vez finalizado el proceso, el carrito del localStorage se limpia y la tablet queda disponible para un nuevo pedido.


### Modelo Entidad-Relación

PRODUCTS N:M CATEGORIES
TABLETS 1:N ORDERS
ORDERS 1:N ORDER_ITEMS
ORDERS 1:1 INVOICES


```text
USERS_ROLES
│
│ 1:N
▼
USERS_VALIDATION
│
│ 1:1
▼
USERS_DETAILS


CATEGORIES
│
│ N:M
▼
PRODUCTS
│
│ 1:N
▼
ORDER_ITEMS
▲
│ 1:N
│
ORDERS
│
├── N:1 ── TABLETS
│
└── 1:1 ── INVOICES
```
