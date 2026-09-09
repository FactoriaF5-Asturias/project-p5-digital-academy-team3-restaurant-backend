# ADAPTACIÓN DE ENTORNO DE TRABAJO PARA USAR LA DB

## CREAR BASE DE DATOS `giacobello`
### Dbeaver
Entrar a Dbeaver y verificar si existe la base de datos
- `SELECT current_database(), current_user;`

#### Posibles errores

**Arrancar el proceso de postgres**
1. Windows + R
2. services.msc
3. Busca el servicio postgresql
4. Inicialo

**Error validación de usuario**
- Modificar el fichero de PATH: PostgreSQL\data
    - pg_hba.conf
        - IPv4 e IPv6: cambiar el scram-sha-256 por trusted, guardar e iniciar sesión sin introducir contraseña.
        - Detener el proceso de postgreSQL
        - `ALTER USER postgres WITH PASSWORD 'postgres';`
        - Cambiar trusted a scram-sha-256
        - Reinicializar el proceso

**"La autentificación password falló para el usuario ${DATABASE_USERNAME}"**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DDATABASE_USERNAME=postgres -DDATABASE_PASSWORD=postgres"
mvn clean install -DDATABASE_USERNAME=postgres -DDATABASE_PASSWORD=postgres
```

**No existe giacobello**
- Si en current_database() aparece postgres:
    - Iniciar sesión y crear la database giacobello (importante llamarla exactamente así para que coincida con la jdbc url de .properties)
    - Verificar su existencia
    - Conectarte a giacobello

**Verificación de DB bien inicializada**
En Dbeaver, ejecutar este comando en giacobello
```sql
SELECT *
FROM flyway_schema_history
ORDER BY installed_rank;
```

Si aparecen las migraciones, es correcto.


## ¿Por que Flyway?

Aparte de que le dimos visto verde cuando lo propusimos en las dependencias. Versiona la base de datos y mejora su rendimiento y escalabilidad. Su principal función es **gestionar el contenido** de la base de datos, pero no crearla. También lo usamos con Hibernate para que este compruebe que el esquema existente sea compatible con las entidades Java.

**IMPORTANTE** Las migraciones y las Entities son distintas y ambas necesarias. La función de entity es la de representar la tabla en Java.