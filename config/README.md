# Start databases locally

To start the different databases used as DataInputProvider, here is a prepared `docker/docker-compose.yml` :
```yml
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
# How to use it
Make sure you have [docker](https://docs.docker.com/install/) and [docker-compose](https://docs.docker.com/compose/install/) installed.

```cd docker``` 
* Run images: ```docker-compose up``` 
* Stop images: ```docker-compose stop``` 
* Remove images: ```docker-compose rm```
## MySQL
MySQL will be started on port 3306. Data initialization is done from `noraui-db.sql` file.
* Server: db_mysql
* User: local
* Password: local
* Database: noraui
## PostgreSQL
PostgreSQL will be started on port 5432. Data initialization is done from `noraui-db.postgre.sql` file.
* Server: db_postgres
* User: local
* Password: local
* Database: noraui
## Adminer
Adminer UI is available at `http://localhost:8080`
