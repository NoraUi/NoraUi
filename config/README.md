

# Start databases locally

To start the different databases used as DataInputProvider, here is a prepared docker-compose.yml :
```yml
<dependency>
    <groupId>com.github.noraui</groupId>
    <artifactId>noraui</artifactId>
    <version>3.3.0-SNAPSHOT</version>
</dependency>
```
version: '3.1'

services:

  db_postgres:
    image: postgres
    restart: always
    environment:
      POSTGRES_USER: local
      POSTGRES_USER: local
      POSTGRES_DB: noraui
    volumes:
            - ../noraui-db.postgre.sql:/docker-entrypoint-initdb.d/noraui-db.sql

  db_mysql:
    image: mysql
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: local
      MYSQL_PASSWORD: local
      MYSQL_DATABASE: noraui
    volumes:
            - ../noraui-db.sql:/docker-entrypoint-initdb.d/noraui-db.sql

```
