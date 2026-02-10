# Práctica Docker - Fase 3.

## Descripción.
- Se ha realizado la práctica del módulo **Despliegue y administración de contenedores**, cuyo objetivo es implementar una Pipeline CI/CD para una aplicación Java haciendo uso de **Docker** y **GitHub Actions**. La aplicación que ha sido desarrollada consta de una API REST basada en un **taller de coches**, donde se gestionan las siguientes entidades:
    - **Coche**.
    - **Mecánico**.
    - **Reparación**.

- El proyecto se ha desarrollado utilizando el framework **Spring-Boot** y generado desde **Spring Initializer**.

## Dockerfile
- El proyecto incluye un **Dockerfile** el cual permite crear una imagen de la aplicación Java que hemos creado. La aplicación se empaqueta como un archivo **WAR** mediante Maven y se despliega sobre un contenedor **Tomcat**. El Dockerfile realiza los siguientes pasos:
    - Utiliza una imagen base de **Tomcat con JDK**.
    - Elimina las aplicaciones por defecto de Tomcat.
    - Copia el archivo WAR generado por Maven al directorio `webapps`.
    - Expone el puerto por el que Tomcat sirve la aplicación.

Esto permite ejecutar la aplicación de forma aislada y reproducible en cualquier entorno compatible con Docker.

## Docker-compose
- Para poder facilitar la ejecución de la aplicación junto a la base de datos se ha realizado este **docker-compose.yml**. Este permite:
    - Definir dependencias entre servicios.
    - Configurar las variables de entorno.
    - Orquestar varios contenedores.

- Concretamente en este proyecto se han usado los siguientes servicios:
    - **Aplicación**: Spring-Boot sobre Tomcat
    - **Base de datos**: MariaDB

- Para ejecutar manualmente este archivo, se debe de usar el siguiente comando:
```
docker compose up -d
```

## Fixtures
- Los fixtures permiten insertar datos de prueba en la base de datos automáticamente al arrancar la aplicación. Estos datos se utilizan para:
    - Comprobar el funcionamiento de los endpoints.
    - Ejecutar los tests sin necesidad de insertar datos a mano.

## Tests
- En este proyecto se han incluido varios tipos distintos de tests, los cuales se ejecutan automaticamente tanto en local como en la implementación del Pipeline CI/CD. Estos tests son los siguientes:
    - Tests unitarios: verifican el funcionamiento de servicios y lógica de negocio de forma aislada.
    - Tests de integración: comprueban la interacción entre la aplicación y la base de datos.
    - Tests de aceptación: validan el funcionamiento de los endpoints.

- Los tests se ejecutan con Maven mediante el comando:
```
mvn clean test
```

## Base de datos
- La base de datos utilizada para esta práctica ha sido MariaDB. En el Pipeline de GitHub Actions se ejecuta lo siguiente cuando hacemos un **push** o un **pull request**:
    - Se despliega un contenedor MariaDB.
    - Se ejecutan los tests una vez la base de datos esté disponible.
    - La aplicación se conecta a la base de datos haciendo uso de las variables de entorno.

## Pipeline CI/CD
- El pipeline se ejecuta automáticamente en cada **push** o **pull request** a la rama **main**, donde se realizan los siguientes pasos:
    - Descarga el código del repositorio (rama main).
    - Configura el JDK 21.
    - Levanta un contenedor MariaDB.
    - Espera a que la base de datos esté lista.
    - Ejecuta los tests haciendo uso de Maven.
    - Construye la imagen Docker de la aplicación.

- Este procedimiento nos garantiza:
    - La aplicación compila correctamente.
    - Todos los tests pasan.
    - La imagen Docker de la aplicación se genera sin errores.

## Construcción de la imagen Docker
- La aplicación se empaqueta como un **WAR** y se despliega en un contenedor **Tomcat**. Para construir la imagen de manera manual, hacemos uso del siguiente comandos:
```
docker build -t taller-app .
```

## Conclusión
- Con esta práctica hemos conseguido:
    - Integrar una aplicación Spring-Boot con Docker.
    - Automatizar la ejecución de tests mediante CI/CD.
    - Garantizamos que el código es de calidad antes de hacer la imagen Docker de la aplicación.