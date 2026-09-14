# Probar la funcionalidad y conexion de endpoints API

#### GET http://localhost:8080/api/orders

#Problema: Error 403 Forbidden

El endpoint `GET /api/orders` requiere autenticación por la regla de Spring Security:

```java
.anyRequest().authenticated()
```

Para probarlo sin autenticación en desarrollo local, añadir en config `SecurityConfig.java`, antes de esa regla:

```java
.requestMatchers(HttpMethod.GET, "/api/orders").permitAll()
```

Añadir también el import:

```java
import org.springframework.http.HttpMethod;
```

> Esta excepción hace público el listado de pedidos. Retirarla después de las pruebas si el endpoint debe requerir autenticación.

### Ejecutar y comprobar

Reiniciar el backend:

```bash
bash mvnw spring-boot:run
```
**si sale error de autenticacion, mirar documento preparacion-entorno-db.md**

Desde otra terminal, consultar el endpoint:

```bash
curl -i http://localhost:8080/api/orders
```

**Respuesta esperada:** `200 OK` con los pedidos en formato JSON o `[]` si no hay registros.
