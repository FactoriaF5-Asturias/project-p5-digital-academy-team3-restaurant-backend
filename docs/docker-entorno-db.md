# Levantar PostgreSQL con Docker para el backend

Esta opcion permite arrancar la base de datos PostgreSQL sin instalar PostgreSQL manualmente en el ordenador.

**NOTA**: NO USAR HASTA HABER TERMINADO MVP.

## Requisitos

- Tener Docker Desktop instalado.
- Tener Docker Desktop iniciado.

## 1. Crear y arrancar el contenedor de PostgreSQL

Ejecutar este comando en la terminal:

```bash
docker run --name giacobello-postgres -e POSTGRES_DB=giacobello -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

Este comando crea una base de datos con estos datos:

```text
host: localhost
port: 5432
database: giacobello
username: postgres
password: postgres
```

## 2. Comprobar que el contenedor esta funcionando

```bash
docker ps
```

Debe aparecer un contenedor llamado:

```text
giacobello-postgres
```

## 3. Arrancar el backend

Desde la carpeta del backend, ejecutar:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments="-DDATABASE_USERNAME=postgres -DDATABASE_PASSWORD=postgres"
```

## Comandos utiles

Si el contenedor ya existe pero esta apagado:

```bash
docker start giacobello-postgres
```

Para parar el contenedor:

```bash
docker stop giacobello-postgres
```

Para borrar el contenedor:

```bash
docker rm giacobello-postgres
```

Despues volver a ejecutar el comando inicial de `docker run`.

