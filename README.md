# Giacobello

Backend de la aplicación de restaurante Giacobello. Expone una API para consultar el catálogo y gestionar pedidos, métodos de pago e información relacionada con las facturas.

## ¿Qué es?

Giacobello es el servicio que conecta la aplicación del restaurante con sus datos. Recibe peticiones de otros clientes —por ejemplo, una interfaz web— y trabaja con la información guardada en PostgreSQL.

## ¿Para qué sirve?

Con la API se pueden consultar productos, categorías, mesas y métodos de pago; crear pedidos y actualizar su estado; y consultar facturas e informes. También incluye inicio de sesión con tokens y operaciones de administración para el catálogo.

La API aplica permisos según el tipo de operación, por lo que algunos endpoints requieren autenticación. La lista de rutas y sus ejemplos está en [`docs/endpoints.md`](docs/endpoints.md).


