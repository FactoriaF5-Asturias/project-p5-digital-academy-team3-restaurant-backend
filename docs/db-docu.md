# Documentación técnica de la base de datos

## Modelo de datos

```text
DB
│
├── users
│   ├── id UUID PK
│   ├── auth_uid VARCHAR UNIQUE NOT NULL
│   ├── email VARCHAR UNIQUE NOT NULL
│   ├── enabled BOOLEAN NOT NULL DEFAULT TRUE
│   ├── role ENUM
│   │   ├── CUSTOMER
│   │   ├── ADMIN
│   │   ├── KITCHEN
│   │   ├── DRIVER
│   │   └── STAFF
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── user_profiles
│   ├── user_id UUID PK/FK → users.id
│   ├── name VARCHAR NOT NULL
│   ├── last_name VARCHAR NOT NULL
│   ├── phone VARCHAR NOT NULL
│   ├── address VARCHAR NOT NULL
│   ├── postal_code VARCHAR NOT NULL
│   ├── city VARCHAR NOT NULL
│   ├── tax_id VARCHAR NULL
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
│
├── categories
│   ├── id UUID PK
│   ├── name VARCHAR UNIQUE NOT NULL
│   ├── display_order INTEGER NOT NULL
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── products
│   ├── id UUID PK
│   ├── category_id UUID FK → categories.id
│   ├── name VARCHAR NOT NULL
│   ├── description TEXT
│   ├── price DECIMAL(12,2) NOT NULL
│   │   └── IVA INCLUIDO
│   ├── tax_rate DECIMAL(5,2) NOT NULL
│   ├── image_url TEXT
│   ├── display_order INTEGER NOT NULL
│   ├── status ENUM
│   │   ├── ACTIVE
│   │   ├── OUT_OF_STOCK
│   │   └── REMOVED
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── allergens
│   ├── id UUID PK
│   ├── code VARCHAR UNIQUE NOT NULL
│   └── name VARCHAR NOT NULL
│
├── product_allergens
│   ├── product_id UUID PK/FK → products.id
│   └── allergen_id UUID PK/FK → allergens.id
│
│
├── product_option_groups
│   ├── id UUID PK
│   ├── product_id UUID FK → products.id
│   ├── name VARCHAR NOT NULL
│   ├── min_selections INTEGER NOT NULL DEFAULT 0
│   ├── max_selections INTEGER NOT NULL DEFAULT 1
│   ├── display_order INTEGER NOT NULL
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── product_options
│   ├── id UUID PK
│   ├── option_group_id UUID FK → product_option_groups.id
│   ├── name VARCHAR NOT NULL
│   ├── price DECIMAL(12,2) NOT NULL DEFAULT 0
│   ├── display_order INTEGER NOT NULL
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
│
├── tables
│   ├── id UUID PK
│   ├── number INTEGER UNIQUE NOT NULL
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── tablets
│   ├── id UUID PK
│   ├── identifier VARCHAR UNIQUE NOT NULL
│   ├── table_id UUID UNIQUE FK → tables.id
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
│
├── carts
│   ├── id UUID PK
│   ├── user_id UUID UNIQUE FK → users.id
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── cart_items
│   ├── id UUID PK
│   ├── cart_id UUID FK → carts.id
│   ├── product_id UUID FK → products.id
│   ├── quantity INTEGER NOT NULL
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│       └── UNIQUE(cart_id, product_id)
│
├── cart_item_options
│   ├── id UUID PK
│   ├── cart_item_id UUID FK → cart_items.id
│   ├── option_id UUID FK → product_options.id
│   ├── option_name VARCHAR NOT NULL
│   ├── price DECIMAL(12,2) NOT NULL
│   └── quantity INTEGER NOT NULL DEFAULT 1
│
│
├── orders
│   ├── id UUID PK
│   ├── user_id UUID FK → users.id
│   ├── type ENUM
│   │   ├── ON_SITE
│   │   └── DELIVERY
│   │
│   ├── table_id UUID NULL FK → tables.id
│   │   └── obligatorio para ON_SITE
│   │
│   ├── delivery_address VARCHAR NULL
│   ├── delivery_postal_code VARCHAR NULL
│   ├── delivery_city VARCHAR NULL
│   ├── delivery_phone VARCHAR NULL
│   │   └── obligatorios para DELIVERY
│   │
│   ├── customer_notes TEXT NULL
│   ├── estimated_ready_at TIMESTAMP NULL
│   ├── ready_at TIMESTAMP NULL
│   │
│   ├── subtotal DECIMAL(12,2) NOT NULL
│   ├── discount_total DECIMAL(12,2) NOT NULL DEFAULT 0
│   ├── delivery_fee DECIMAL(12,2) NOT NULL DEFAULT 0
│   ├── tax_total DECIMAL(12,2) NOT NULL DEFAULT 0
│   ├── total DECIMAL(12,2) NOT NULL
│   │
│   ├── status ENUM
│   │   ├── PENDING
│   │   ├── IN_PROGRESS
│   │   ├── READY
│   │   ├── IN_TRANSIT
│   │   ├── DELIVERED
│   │   ├── DELIVERY_FAILED
│   │   └── CANCELLED
│   │
│   ├── payment_status ENUM
│   │   ├── PENDING
│   │   ├── PAID
│   │   ├── FAILED
│   │   └── REFUNDED
│   │
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── order_items
│   ├── id UUID PK
│   ├── order_id UUID FK → orders.id
│   ├── product_id UUID NULL FK → products.id
│   ├── product_name VARCHAR NOT NULL
│   ├── quantity INTEGER NOT NULL
│   ├── unit_price DECIMAL(12,2) NOT NULL
│   │   └── IVA INCLUIDO
│   ├── tax_rate DECIMAL(5,2) NOT NULL
│   ├── applied_offer_id UUID NULL FK → offers.id
│   ├── discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0
│   └── line_total DECIMAL(12,2) NOT NULL
│
├── order_item_options
│   ├── id UUID PK
│   ├── order_item_id UUID FK → order_items.id
│   ├── option_id UUID NULL FK → product_options.id
│   ├── option_name VARCHAR NOT NULL
│   ├── unit_price DECIMAL(12,2) NOT NULL
│   ├── quantity INTEGER NOT NULL DEFAULT 1
│   └── total_price DECIMAL(12,2) NOT NULL
│
├── order_status_history
│   ├── id UUID PK
│   ├── order_id UUID FK → orders.id
│   ├── status ENUM
│   │   ├── PENDING
│   │   ├── IN_PROGRESS
│   │   ├── READY
│   │   ├── IN_TRANSIT
│   │   ├── DELIVERED
│   │   ├── DELIVERY_FAILED
│   │   └── CANCELLED
│   ├── changed_at TIMESTAMP NOT NULL
│   ├── changed_by UUID FK → users.id
│   └── note TEXT NULL
│
│
├── payments
│   ├── id UUID PK
│   ├── order_id UUID FK → orders.id
│   ├── method ENUM
│   │   ├── CARD
│   │   └── CASH
│   ├── status ENUM
│   │   ├── PENDING
│   │   ├── PAID
│   │   ├── FAILED
│   │   └── REFUNDED
│   ├── amount DECIMAL(12,2) NOT NULL
│   ├── currency VARCHAR(3) NOT NULL DEFAULT 'EUR'
│   ├── provider VARCHAR NULL
│   │   └── STRIPE
│   ├── provider_payment_id VARCHAR NULL
│   ├── provider_transaction_id VARCHAR NULL
│   ├── card_brand VARCHAR NULL
│   ├── card_last4 VARCHAR(4) NULL
│   ├── failure_reason TEXT NULL
│   ├── paid_at TIMESTAMP NULL
│   └── created_at TIMESTAMP NOT NULL
│
├── payment_refunds
│   ├── id UUID PK
│   ├── payment_id UUID FK → payments.id
│   ├── amount DECIMAL(12,2) NOT NULL
│   ├── status ENUM
│   │   ├── PENDING
│   │   ├── SUCCEEDED
│   │   └── FAILED
│   ├── provider VARCHAR NOT NULL
│   ├── provider_refund_id VARCHAR UNIQUE
│   ├── reason TEXT NULL
│   ├── created_at TIMESTAMP NOT NULL
│   └── completed_at TIMESTAMP NULL
│
│
├── deliveries
│   ├── id UUID PK
│   ├── order_id UUID UNIQUE FK → orders.id
│   ├── driver_id UUID FK → users.id
│   ├── assigned_at TIMESTAMP NULL
│   ├── picked_up_at TIMESTAMP NULL
│   ├── delivered_at TIMESTAMP NULL
│   └── failure_reason TEXT NULL
│
│
├── invoices
│   ├── id UUID PK
│   ├── order_id UUID UNIQUE FK → orders.id
│   ├── series VARCHAR NOT NULL
│   ├── number BIGINT NOT NULL
│   ├── fiscal_year INTEGER NOT NULL
│   │   └── UNIQUE(series, fiscal_year, number)
│   │
│   ├── type ENUM
│   │   ├── SIMPLIFIED
│   │   ├── STANDARD
│   │   └── CORRECTIVE
│   │
│   ├── corrects_invoice_id UUID NULL FK → invoices.id
│   │
│   ├── customer_name VARCHAR NULL
│   ├── customer_tax_id VARCHAR NULL
│   ├── customer_address VARCHAR NULL
│   ├── customer_postal_code VARCHAR NULL
│   ├── customer_city VARCHAR NULL
│   │
│   ├── taxable_base DECIMAL(12,2) NOT NULL
│   ├── tax_total DECIMAL(12,2) NOT NULL
│   ├── total DECIMAL(12,2) NOT NULL
│   │
│   ├── issued_at TIMESTAMP NOT NULL
│   ├── pdf_storage_path TEXT NULL
│   └── pdf_url TEXT NULL
│
├── invoice_items
│   ├── id UUID PK
│   ├── invoice_id UUID FK → invoices.id
│   ├── description VARCHAR NOT NULL
│   ├── quantity INTEGER NOT NULL
│   ├── unit_price DECIMAL(12,2) NOT NULL
│   ├── tax_rate DECIMAL(5,2) NOT NULL
│   ├── taxable_base DECIMAL(12,2) NOT NULL
│   ├── tax_amount DECIMAL(12,2) NOT NULL
│   └── line_total DECIMAL(12,2) NOT NULL
│
├── invoice_tax_lines
│   ├── id UUID PK
│   ├── invoice_id UUID FK → invoices.id
│   ├── tax_rate DECIMAL(5,2) NOT NULL
│   ├── taxable_base DECIMAL(12,2) NOT NULL
│   ├── tax_amount DECIMAL(12,2) NOT NULL
│   └── UNIQUE(invoice_id, tax_rate)
│
│
├── offers
│   ├── id UUID PK
│   ├── code VARCHAR UNIQUE NOT NULL
│   ├── title VARCHAR NOT NULL
│   ├── description TEXT NULL
│   ├── discount_type ENUM
│   │   ├── PERCENTAGE
│   │   └── FIXED
│   ├── discount_value DECIMAL(12,2) NOT NULL
│   ├── target_type ENUM
│   │   ├── PRODUCT
│   │   ├── CATEGORY
│   │   └── GLOBAL
│   ├── target_id UUID NULL
│   ├── priority INTEGER NOT NULL DEFAULT 0
│   ├── max_total_uses INTEGER NULL
│   ├── total_uses INTEGER NOT NULL DEFAULT 0
│   ├── start_date TIMESTAMP NOT NULL
│   ├── end_date TIMESTAMP NULL
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
├── offer_redemptions
│   ├── id UUID PK
│   ├── offer_id UUID FK → offers.id
│   ├── user_id UUID FK → users.id
│   ├── order_id UUID UNIQUE FK → orders.id
│   ├── discount_amount DECIMAL(12,2) NOT NULL
│   └── redeemed_at TIMESTAMP NOT NULL
│
│
├── notifications
│   ├── id UUID PK
│   ├── user_id UUID FK → users.id
│   ├── type ENUM
│   │   ├── ORDER_ACCEPTED
│   │   ├── ORDER_IN_TRANSIT
│   │   ├── ORDER_DELIVERED
│   │   └── GENERAL
│   ├── title VARCHAR NOT NULL
│   ├── message TEXT NOT NULL
│   ├── read_at TIMESTAMP NULL
│   └── created_at TIMESTAMP NOT NULL
│
├── push_subscriptions
│   ├── id UUID PK
│   ├── user_id UUID FK → users.id
│   ├── endpoint TEXT NULL
│   ├── public_key TEXT NULL
│   ├── auth_key TEXT NULL
│   ├── fcm_token TEXT NULL
│   ├── active BOOLEAN NOT NULL DEFAULT TRUE
│   ├── created_at TIMESTAMP NOT NULL
│   └── updated_at TIMESTAMP NOT NULL
│
│
├── audit_logs
│   ├── id UUID PK
│   ├── user_id UUID NULL FK → users.id
│   ├── action VARCHAR NOT NULL
│   ├── entity_type VARCHAR NOT NULL
│   ├── entity_id UUID NOT NULL
│   ├── old_values JSONB NULL
│   ├── new_values JSONB NULL
│   ├── ip_address VARCHAR NULL
│   ├── user_agent TEXT NULL
│   └── created_at TIMESTAMP NOT NULL
│
│
└── sales_reports
    ├── id UUID PK
    ├── period_type ENUM
    │   ├── DAILY
    │   ├── MONTHLY
    │   ├── QUARTERLY
    │   └── ANNUAL
    ├── period_start TIMESTAMP NOT NULL
    ├── period_end TIMESTAMP NOT NULL
    │
    ├── gross_sales DECIMAL(12,2) NOT NULL
    ├── discount_total DECIMAL(12,2) NOT NULL
    ├── net_sales DECIMAL(12,2) NOT NULL
    ├── tax_total DECIMAL(12,2) NOT NULL
    ├── total_orders INTEGER NOT NULL
    ├── delivery_orders INTEGER NOT NULL
    ├── delivery_sales DECIMAL(12,2) NOT NULL
    ├── onsite_orders INTEGER NOT NULL
    ├── onsite_sales DECIMAL(12,2) NOT NULL
    │
    ├── pdf_storage_path TEXT NOT NULL
    ├── pdf_url TEXT NULL
    ├── generated_by UUID NULL FK → users.id
    └── generated_at TIMESTAMP NOT NULL
```

## Restricciones e índices recomendados

- `cart_items` debe tener una única línea por producto y carrito mediante `UNIQUE(cart_id, product_id)`. La cantidad se controla con el campo `quantity`.
- Los pedidos `ON_SITE` deben tener `table_id` y los pedidos `DELIVERY` deben tener `delivery_address`, `delivery_postal_code`, `delivery_city` y `delivery_phone`.
- Las cantidades deben ser mayores que cero y los precios, impuestos, descuentos y gastos no pueden ser negativos.
- `min_selections` no puede ser mayor que `max_selections`.

Índices recomendados para acelerar las consultas habituales:

Los índices son estructuras que aceleran las búsquedas, como el índice de un libro. Por ejemplo, buscar rápidamente pedidos por usuario, estado o fecha. No crean funcionalidades, solo mejoran el rendimiento.

```sql
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
```

## Documentación de campos

### users

Almacena la información de autenticación y configuración de los usuarios del sistema.
Permite identificar el usuario, controlar su acceso y determinar sus permisos mediante el rol.

**id**: Identificador único del usuario.
**auth_uid**: Identificador único del usuario en el sistema de autenticación externo.
**email**: Correo electrónico único utilizado por el usuario.
**enabled**: Indica si la cuenta del usuario está habilitada.
**role**: Define el rol del usuario: CUSTOMER, ADMIN, KITCHEN, DRIVER o STAFF.
**created_at**: Fecha y hora de creación del usuario.
**updated_at**: Fecha y hora de la última modificación.

### user_profiles

Contiene los datos personales asociados a un usuario.
Se separa de users para mantener independientes los datos de autenticación y la información personal.

**user_id**: Identificador del usuario y clave primaria y foránea hacia users.
**name**: Nombre del usuario.
**last_name**: Apellidos del usuario.
**phone**: Teléfono de contacto.
**address**: Dirección habitual del usuario.
**postal_code**: Código postal de la dirección.
**city**: Ciudad de residencia.
**tax_id**: NIF u otro identificador fiscal, si procede.
**created_at**: Fecha y hora de creación del perfil.
**updated_at**: Fecha y hora de la última modificación.

### categories

Representa las categorías utilizadas para organizar los productos del menú.
Permite ordenar y activar o desactivar categorías sin eliminar sus datos.

**id**: Identificador único de la categoría.
**name**: Nombre único de la categoría.
**display_order**: Orden en el que se muestra la categoría.
**active**: Indica si la categoría está disponible.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### products

Contiene los productos disponibles en el menú del restaurante.
Almacena el precio actual, IVA, información descriptiva, imagen y estado del producto.

**id**: Identificador único del producto.
**category_id**: Categoría a la que pertenece el producto.
**name**: Nombre actual del producto.
**description**: Descripción del producto.
**price**: Precio actual del producto con IVA incluido.
**tax_rate**: Porcentaje de IVA aplicado al producto.
**image_url**: URL de la imagen del producto.
**display_order**: Orden en el que se muestra dentro de su categoría.
**status**: Estado del producto: ACTIVE, OUT_OF_STOCK o REMOVED.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### allergens

Catálogo de alérgenos que pueden estar asociados a los productos.
Permite informar de forma estructurada sobre los alérgenos presentes en el menú.

**id**: Identificador único del alérgeno.
**code**: Código único del alérgeno.
**name**: Nombre del alérgeno.

### product_allergens

Tabla intermedia que relaciona productos con alérgenos.
Permite representar la relación muchos a muchos entre products y allergens.

**product_id**: Producto asociado.
**allergen_id**: Alérgeno asociado.

### product_option_groups

Define grupos de opciones personalizables disponibles para un producto.
Permite establecer reglas como el número mínimo y máximo de opciones que puede seleccionar el cliente.

**id**: Identificador único del grupo.
**product_id**: Producto al que pertenece el grupo.
**name**: Nombre del grupo de opciones.
**min_selections**: Número mínimo de opciones que deben seleccionarse.
**max_selections**: Número máximo de opciones que pueden seleccionarse.
**display_order**: Orden de visualización del grupo.
**active**: Indica si el grupo está disponible.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### product_options

Contiene las opciones individuales que pueden seleccionarse dentro de un grupo.
Cada opción puede tener un precio adicional respecto al producto base.

**id**: Identificador único de la opción.
**option_group_id**: Grupo al que pertenece la opción.
**name**: Nombre de la opción.
**price**: Precio adicional de la opción.
**display_order**: Orden de visualización.
**active**: Indica si la opción está disponible.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### tables

Representa las mesas físicas disponibles en el restaurante.
Cada mesa tiene un número único y puede estar asociada opcionalmente a una tablet.

**id**: Identificador único de la mesa.
**number**: Número único de la mesa.
**active**: Indica si la mesa está actualmente disponible.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### tablets

Representa las tablets instaladas en las mesas del restaurante.
Una tablet puede asociarse a una única mesa mediante table_id.

**id**: Identificador único de la tablet.
**identifier**: Identificador único del dispositivo.
**table_id**: Mesa asociada a la tablet.
**active**: Indica si la tablet está activa.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### carts

Representa el carrito actual de compra de un usuario.
Cada usuario puede tener como máximo un carrito activo asociado.

**id**: Identificador único del carrito.
**user_id**: Usuario propietario del carrito.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### cart_items

Contiene los productos incluidos actualmente en un carrito.
Cada producto aparece una sola vez por carrito y su cantidad se controla mediante `quantity`.

**id**: Identificador único de la línea del carrito.
**cart_id**: Carrito al que pertenece la línea.
**product_id**: Producto seleccionado.
**quantity**: Cantidad del producto.
**created_at**: Fecha y hora de creación de la línea.
**updated_at**: Fecha y hora de la última modificación.

Restricción: UNIQUE(cart_id, product_id), según la estructura propuesta del árbol de la base de datos.

### cart_item_options

Almacena las opciones seleccionadas para una línea concreta del carrito.
También conserva el nombre y precio de la opción utilizados en esa configuración.

**id**: Identificador único de la opción seleccionada.
**cart_item_id**: Línea del carrito a la que pertenece.
**option_id**: Opción original seleccionada.
**option_name**: Nombre de la opción almacenado en el carrito.
**price**: Precio de la opción en el momento de añadirla.
**quantity**: Cantidad seleccionada de la opción.

### orders

Representa los pedidos realizados por los clientes.
Contiene tanto pedidos realizados en el restaurante como pedidos a domicilio, además de sus importes y estados actuales.

**id**: Identificador único del pedido.
**user_id**: Usuario que realizó el pedido.
**type**: Tipo de pedido: ON_SITE o DELIVERY.
**table_id**: Mesa asociada al pedido cuando es ON_SITE.
**delivery_address**: Dirección de entrega cuando el pedido es DELIVERY.
**delivery_postal_code**: Código postal de entrega.
**delivery_city**: Ciudad de entrega.
**delivery_phone**: Teléfono utilizado para la entrega.
**customer_notes**: Observaciones introducidas por el cliente.
**estimated_ready_at**: Fecha y hora estimada de preparación.
**ready_at**: Fecha y hora real en la que el pedido estuvo preparado.
**subtotal**: Importe de los productos antes de descuentos y otros conceptos.
**discount_total**: Descuento total aplicado al pedido.
**delivery_fee**: Coste de entrega.
**tax_total**: Importe total correspondiente al IVA.
**total**: Importe final del pedido.
**status**: Estado actual del pedido.
**payment_status**: Estado oficial del pago del pedido.
**created_at**: Fecha y hora de creación del pedido.
**updated_at**: Fecha y hora de la última modificación.

### order_items

Contiene las líneas de productos de un pedido y constituye su snapshot histórico.
Aunque el producto cambie posteriormente, estos datos mantienen el nombre, precio, IVA y descuento originales.

**id**: Identificador único de la línea.
**order_id**: Pedido al que pertenece la línea.
**product_id**: Producto original asociado, si todavía existe.
**product_name**: Nombre del producto almacenado históricamente.
**quantity**: Cantidad solicitada.
**unit_price**: Precio unitario histórico con IVA incluido.
**tax_rate**: IVA aplicado en el momento de la compra.
**applied_offer_id**: Oferta aplicada a la línea, si existe.
**discount_amount**: Descuento aplicado a la línea.
**line_total**: Importe total de la línea.

### order_item_options

Contiene las opciones seleccionadas para una línea de pedido.
Almacena un snapshot de las opciones para que los pedidos históricos no dependan de los precios actuales.

**id**: Identificador único de la opción de la línea.
**order_item_id**: Línea de pedido a la que pertenece.
**option_id**: Opción original, si todavía existe.
**option_name**: Nombre histórico de la opción.
**unit_price**: Precio unitario histórico de la opción.
**quantity**: Cantidad seleccionada.
**total_price**: Importe total de la opción.

### order_status_history

Registra todos los cambios de estado realizados sobre los pedidos.
Permite conocer el ciclo de vida completo de un pedido y quién realizó cada cambio.

**id**: Identificador único del registro histórico.
**order_id**: Pedido cuyo estado ha cambiado.
**status**: Estado registrado.
**changed_at**: Fecha y hora del cambio.
**changed_by**: Usuario que realizó el cambio.
**note**: Nota opcional asociada al cambio.

### payments

Registra los intentos y operaciones de pago asociados a los pedidos.
Puede existir más de un pago para un pedido, por ejemplo, si un primer intento con tarjeta falla.

**id**: Identificador único del pago.
**order_id**: Pedido asociado al pago.
**method**: Método de pago: CARD o CASH.
**status**: Estado de esta operación de pago.
**amount**: Importe de la operación.
**currency**: Moneda utilizada, normalmente EUR.
**provider**: Proveedor de pago, como STRIPE.
**provider_payment_id**: Identificador del pago en el proveedor.
**provider_transaction_id**: Identificador de la transacción en el proveedor.
**card_brand**: Marca de la tarjeta utilizada.
**card_last4**: Últimos cuatro dígitos de la tarjeta.
**failure_reason**: Motivo del fallo del pago.
**paid_at**: Fecha y hora en que el pago fue completado.
**created_at**: Fecha y hora de creación del registro.

### payment_refunds

Registra los reembolsos realizados sobre un pago.
Permite gestionar reembolsos completos o parciales y conservar su estado.

**id**: Identificador único del reembolso.
**payment_id**: Pago sobre el que se realiza el reembolso.
**amount**: Importe reembolsado.
**status**: Estado del reembolso.
**provider**: Proveedor que procesa el reembolso.
**provider_refund_id**: Identificador del reembolso en el proveedor.
**reason**: Motivo del reembolso.
**created_at**: Fecha y hora de creación.
**completed_at**: Fecha y hora en que se completó el reembolso.

### deliveries

Gestiona la entrega de los pedidos a domicilio.
Permite controlar el conductor asignado y las diferentes etapas de recogida y entrega.

**id**: Identificador único de la entrega.
**order_id**: Pedido asociado a la entrega.
**driver_id**: Usuario con rol DRIVER encargado de la entrega.
**assigned_at**: Fecha y hora de asignación del conductor.
**picked_up_at**: Fecha y hora de recogida del pedido.
**delivered_at**: Fecha y hora de entrega.
**failure_reason**: Motivo por el que la entrega no pudo completarse.

### invoices

Representa las facturas generadas para los pedidos.
Conserva sus propios datos fiscales e históricos para que una factura no cambie aunque posteriormente se modifiquen usuarios o productos.

**id**: Identificador único de la factura.
**order_id**: Pedido al que corresponde la factura.
**series**: Serie fiscal de la factura.
**number**: Número de factura.
**fiscal_year**: Año fiscal de la factura.
**type**: Tipo de factura: SIMPLIFIED, STANDARD o CORRECTIVE.
**corrects_invoice_id**: Factura original que corrige, cuando corresponde.
**customer_name**: Nombre del cliente almacenado en la factura.
**customer_tax_id**: NIF del cliente almacenado históricamente.
**customer_address**: Dirección fiscal almacenada en la factura.
**customer_postal_code**: Código postal almacenado en la factura.
**customer_city**: Ciudad almacenada en la factura.
**taxable_base**: Base imponible total.
**tax_total**: IVA total de la factura.
**total**: Importe total de la factura.
**issued_at**: Fecha y hora de emisión.
**pdf_storage_path**: Ruta interna donde se almacena el PDF.
**pdf_url**: URL utilizada para acceder al PDF.

### invoice_items

Contiene las líneas históricas de una factura.
Los datos se almacenan independientemente de los productos actuales para preservar el contenido fiscal original.

**id**: Identificador único de la línea.
**invoice_id**: Factura a la que pertenece.
**description**: Descripción histórica del producto o servicio.
**quantity**: Cantidad facturada.
**unit_price**: Precio unitario facturado.
**tax_rate**: Tipo de IVA aplicado.
**taxable_base**: Base imponible de la línea.
**tax_amount**: Importe de IVA de la línea.
**line_total**: Importe total de la línea.

### invoice_tax_lines

Contiene el desglose del IVA de una factura por tipo impositivo.
Permite separar las bases imponibles y cuotas correspondientes a cada porcentaje de IVA.

**id**: Identificador único de la línea de IVA.
**invoice_id**: Factura a la que pertenece.
**tax_rate**: Tipo de IVA.
**taxable_base**: Base imponible asociada al tipo de IVA.
**tax_amount**: Cuota de IVA correspondiente.

### offers

Representa las promociones disponibles en el sistema.
Una oferta puede aplicar un porcentaje o una cantidad fija y dirigirse a un producto, una categoría o globalmente.

**id**: Identificador único de la oferta.
**code**: Código único de la promoción.
**title**: Nombre o título de la oferta.
**description**: Descripción de la promoción.
**discount_type**: Tipo de descuento: PERCENTAGE o FIXED.
**discount_value**: Valor del descuento.
**target_type**: Ámbito de aplicación: PRODUCT, CATEGORY o GLOBAL.
**target_id**: Identificador del producto o categoría objetivo cuando corresponde.
**priority**: Prioridad utilizada para resolver varias ofertas.
**max_total_uses**: Número máximo de usos permitidos.
**total_uses**: Número de usos realizados.
**start_date**: Fecha y hora desde la que está activa.
**end_date**: Fecha y hora hasta la que está activa.
**active**: Indica si la oferta está habilitada.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### offer_redemptions

Registra cada utilización de una oferta por parte de un usuario en un pedido.
Permite mantener un histórico de las promociones aplicadas y del descuento obtenido.

**id**: Identificador único de la utilización.
**offer_id**: Oferta utilizada.
**user_id**: Usuario que utilizó la oferta.
**order_id**: Pedido en el que se utilizó.
**discount_amount**: Importe de descuento aplicado.
**redeemed_at**: Fecha y hora de utilización.

### notifications

Almacena las notificaciones que recibe cada usuario.
Permite informar sobre cambios relevantes en pedidos y enviar comunicaciones generales.

**id**: Identificador único de la notificación.
**user_id**: Usuario destinatario.
**type**: Tipo de notificación.
**title**: Título mostrado al usuario.
**message**: Contenido de la notificación.
**read_at**: Fecha y hora en que el usuario la leyó.
**created_at**: Fecha y hora de creación.

### push_subscriptions

Almacena las suscripciones necesarias para enviar notificaciones push a los dispositivos de los usuarios.
Un usuario puede disponer de varias suscripciones correspondientes a diferentes dispositivos o navegadores.

**id**: Identificador único de la suscripción.
**user_id**: Usuario propietario de la suscripción.
**endpoint**: Endpoint utilizado para Web Push.
**public_key**: Clave pública de la suscripción Web Push.
**auth_key**: Clave de autenticación de la suscripción.
**fcm_token**: Token utilizado por Firebase Cloud Messaging.
**active**: Indica si la suscripción sigue activa.
**created_at**: Fecha y hora de creación.
**updated_at**: Fecha y hora de la última modificación.

### audit_logs

Registra acciones importantes realizadas en el sistema para proporcionar trazabilidad.
Permite saber quién realizó una acción, sobre qué entidad, cuándo y qué valores fueron modificados.

**id**: Identificador único del registro de auditoría.
**user_id**: Usuario que realizó la acción, si existe.
**action**: Acción realizada.
**entity_type**: Tipo de entidad afectada.
**entity_id**: Identificador de la entidad afectada.
**old_values**: Valores anteriores almacenados en formato JSON.
**new_values**: Valores nuevos almacenados en formato JSON.
**ip_address**: Dirección IP desde la que se realizó la acción.
**user_agent**: Información del navegador o cliente utilizado.
**created_at**: Fecha y hora de la acción.

### sales_reports

Almacena permanentemente los informes automáticos de ventas.
Los datos agregados permiten consultar el histórico de ventas por periodos diarios, mensuales, trimestrales y anuales.

**id**: Identificador único del informe.
**period_type**: Periodicidad del informe: DAILY, MONTHLY, QUARTERLY o ANNUAL.
**period_start**: Inicio del periodo analizado.
**period_end**: Fin del periodo analizado.
**gross_sales**: Importe bruto de las ventas.
**discount_total**: Total de descuentos aplicados.
**net_sales**: Importe neto de ventas.
**tax_total**: Total de IVA generado.
**total_orders**: Número total de pedidos.
**delivery_orders**: Número de pedidos a domicilio.
**delivery_sales**: Ventas correspondientes a pedidos a domicilio.
**onsite_orders**: Número de pedidos realizados en el restaurante.
**onsite_sales**: Ventas correspondientes a pedidos realizados en el restaurante.
**pdf_storage_path**: Ruta interna donde se almacena el PDF.
**pdf_url**: URL para acceder al PDF.
**generated_by**: Usuario que generó el informe, si procede.
**generated_at**: Fecha y hora de generación del informe.

## Relaciones entre entidades

- **USER** 1:0..1 **USER_PROFILE**
- **CATEGORY** 1:N **PRODUCT**
- **PRODUCT** N:N **ALLERGEN**, mediante **PRODUCT_ALLERGEN**
- **PRODUCT** 1:N **PRODUCT_OPTION_GROUP** 1:N **PRODUCT_OPTION**
- **TABLE** 1:0..1 **TABLET**
- **USER** 1:0..1 **CART** 1:N **CART_ITEM** N:1 **PRODUCT**
- **CART_ITEM** 1:N **CART_ITEM_OPTION**
- **USER** 1:N **ORDER**
- **TABLE** 1:N **ORDER** cuando el pedido es `ON_SITE`
- **ORDER** 1:N **ORDER_ITEM** N:1 **PRODUCT**
- **ORDER_ITEM** 1:N **ORDER_ITEM_OPTION**
- **ORDER** 1:N **PAYMENT**
- **PAYMENT** 1:N **PAYMENT_REFUND**
- **ORDER** 1:N **ORDER_STATUS_HISTORY** N:1 **USER** como usuario que realiza el cambio
- **ORDER** 1:0..1 **DELIVERY** N:1 **USER** como repartidor
- **ORDER** 1:0..1 **INVOICE**
- **INVOICE** 1:N **INVOICE_ITEM**
- **INVOICE** 1:N **INVOICE_TAX_LINE**
- **INVOICE** 1:N **INVOICE** como facturas rectificativas mediante `corrects_invoice_id`
- **OFFER** 1:N **OFFER_REDEMPTION**
- **USER** 1:N **OFFER_REDEMPTION**
- **ORDER** 1:0..1 **OFFER_REDEMPTION**
- **OFFER** puede dirigirse a un **PRODUCT**, una **CATEGORY** o ser `GLOBAL`, mediante `target_type` y `target_id`
- **USER** 1:N **NOTIFICATION**
- **USER** 1:N **PUSH_SUBSCRIPTION**
- **USER** 1:N **AUDIT_LOG**
- **USER** 1:N **SALES_REPORT** como usuario que genera el informe

> **Leyenda:** `1:0..1` indica que una entidad puede tener o no esa propiedad.