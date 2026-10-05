# Probar la funcionalidad y conexion de endpoints API

#### GET http://localhost:8080/api/orders

#Problema: Error 403 Forbidden

Los endpoints `GET /api/orders`, `GET /api/categories` requieren autenticación por la regla de Spring Security:

Para probarlo sin autenticación en desarrollo local, añadir en config `SecurityConfig.java`

> Esta excepción hace público el listado de pedidos. Retirarla después de las pruebas si el endpoint debe requerir autenticación.

### Ejecutar y comprobar

Reiniciar el backend:

```bash
bash mvnw spring-boot:run
```
**si sale error de autenticacion, mirar documento preparacion-entorno-db.md**

Desde otra terminal, consultar el endpoint:

```bash
curl -i http://localhost:8080/api/v1/orders
```
o
```bash
curl -i http://localhost:8080/api/v1/categories
```
**Respuesta esperada:** `200 OK` con los pedidos/categorias en formato JSON o `[]` si no hay registros.
