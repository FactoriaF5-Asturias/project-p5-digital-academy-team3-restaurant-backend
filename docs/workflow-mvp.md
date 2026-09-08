# MVP

## Flujo de trabajo

Se ha decidido realizar el flujo de usuario completo desde menu hasta realizar la compra de un pedido en efectivo ON_SITE

**Workflow**: Frontend > Seguridad > Controller > Service > Repository > DB

### TABLAS OBLIGATORIAS PARA MVP
- PRODUCTS: catálogo de producto
    - id PK
    - name VARCHAR
    - description TEXT
    - category_id FK
    - price DECIMAL(10,2)
    - status boolean
- CATEGORIES: Definir tipo de productos
    - id PK
    - name VARCHAR
- TABLETS: Tablet de la mesa donde se realizan pedidos
    - id PK
    - name VARCHAR
- STATUS: Estado de pedido
    - name UNIQUE VARCHAR 
    - id PK
- PAYMENT_METHOD: Tipo de pago
    - id PK
    - name VARCHAR UNIQUE
- ORDERS: Pedido hecho por cliente
    - id PK
    - status_name FK
    - order_type_name FK 
    - payment_method_name FK
    - total_amount DECIMAL(10,2)
    - created_at TIMESTAMP
- ORDER_ITEMS: Productos, cantidades y precio de cada producto incluido en un pedido.
    - id PK
    - order_id FK
    - product_id FK
    - quantity INT
    - unit_price DECIMAL(10,2)
    - subtotal DECIMAL(10,2)
- ORDER_TYPE: Tipo de pedido
    - id PK
    - name VARCHAR UNIQUE
- INVOICES: Factura de pedido
    - id PK 
    - order_id FK UNIQUE
    - invoice_number VARCHAR
    - total_amount DECIMAL(10,2)
    - issued_at TIMESTAMP
Atajos: 
- Delegar carrito a frontend - Al confirmar el pedido y realizar el pago, se crea la ORDER junto con sus ORDER_ITEMS y su INVOICE.
- ORDERS: Hacer exclusivamente la lógica para ON_SITE

**NOTA**: Una vez finalizado el proceso, el carrito del localStorage se limpia y la tablet queda disponible para un nuevo pedido.


### Modelo Entidad-Relación

TABLETS 1:N ORDERS
ORDERS 1:N ORDER_ITEMS
ORDERS 1:1 INVOICES
ORDER_TYPE 1:N ORDERS
PAYMENT_METHOD 1:N ORDERS
STATUS 1:N ORDERS
PRODUCTS 1:N ORDER_ITEMS
PRODUCTS N:1 CATEGORIES



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
