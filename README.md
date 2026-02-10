# Práctica Docker - Fase 3.

## Descripción.
- Se ha realizado la práctica del módulo **Despliegue y administración de contenedores**, cuyo objetivo es implementar una Pipeline CI/CD para una aplicación Java haciendo uso de **Docker** y **GitHub Actions**. La aplicación que ha sido desarrollada consta de una API REST basada en un **taller de coches**, donde se gestionan las siguientes entidades:
    - **Coche**.
    - **Mecánico**.
    - **Reparación**.

- El proyecto se ha desarrollado utilizando el framework **Spring-Boot** y generado desde **Spring Initializer**.

## Estructura del proyecto

```
taller/taller/
├── src/
│   ├── main/java/mateolopez/taller/
│   │   ├── TallerApplication.java          # Punto de entrada de la aplicación
│   │   ├── ServletInitializer.java          # Inicializador del servlet para WAR
│   │   ├── controller/                      # Controladores REST
│   │   │   ├── CocheController.java
│   │   │   ├── MecanicoController.java
│   │   │   └── ReparacionController.java
│   │   ├── model/                           # Entidades JPA
│   │   │   ├── Coche.java
│   │   │   ├── Mecanico.java
│   │   │   └── Reparacion.java
│   │   ├── repository/                      # Repositorios JPA
│   │   │   ├── CocheRepository.java
│   │   │   ├── MecanicoRepository.java
│   │   │   └── ReparacionRepository.java
│   │   └── service/                         # Servicios de negocio
│   │       ├── CocheService.java
│   │       ├── MecanicoService.java
│   │       └── ReparacionService.java
│   ├── main/resources/
│   │   └── application.properties           # Configuración principal (MariaDB)
│   └── test/
│       ├── java/mateolopez/taller/
│       │   ├── TallerApplicationTests.java  # Test de carga del contexto
│       │   ├── Tests.java                   # Tests unitarios, integración y aceptación
│       │   └── fixtures/
│       │       └── DataFixtures.java        # Datos de prueba (~10 registros por tabla)
│       └── resources/
│           └── application-test.properties  # Configuración de test (H2 en memoria)
├── Dockerfile
├── docker-compose.yaml
├── checkstyle.xml
└── pom.xml
```

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

### Poner en marcha la aplicación

```bash
# 1. Empaquetar la aplicación
cd taller/taller
mvn clean package -DskipTests

# 2. Levantar los contenedores (MariaDB + Tomcat)
docker compose up -d

# 3. Verificar que los contenedores están corriendo
docker compose ps
```

La aplicación estará disponible en `http://localhost:8080`.

Para detener los contenedores:
```bash
docker compose down
```

## Fixtures
- Los fixtures permiten insertar datos de prueba en la base de datos automáticamente al arrancar la aplicación. Estos datos se utilizan para:
    - Comprobar el funcionamiento de los endpoints.
    - Ejecutar los tests sin necesidad de insertar datos a mano.

- Se cargan automáticamente a través de la clase `DataFixtures.java` que inserta:
    - **10 coches** con matrículas únicas.
    - **5 mecánicos** con distintas especialidades.
    - **10 reparaciones** asociando coches y mecánicos.

## Tests
- En este proyecto se han incluido varios tipos distintos de tests, los cuales se ejecutan automáticamente tanto en local como en la implementación del Pipeline CI/CD.

### Ejecutar los tests

```bash
# Ejecutar todos los tests
cd taller/taller
mvn clean test

# Ejecutar solo tests unitarios
mvn test -Dtest="TallerApplicationTests,Tests#testCantidadCoches+testCocheNoNull+testIdsCoche+testMatriculasUnicas+testAddCar+testUpdateCar+testDeleteCar+testAddMecanico+testUpdateMecanico+testDeleteMecanico+testCrearCocheDuplicadoMatricula+testCrearMecanicoDuplicadoNombre"

# Ejecutar solo tests de integración
mvn test -Dtest="Tests#testRelacionCocheReparacion+testRelacionCocheMultipleReparaciones+testAddReparacion+testActualizarReparacion+testEliminarReparacion+testCrearReparacionSinCoche+testCrearReparacionSinMecanico"

# Ejecutar solo tests de aceptación
mvn test -Dtest="Tests#testCrearVariasReparaciones+testEliminarCocheConReparaciones+testEliminarMecanicoConReparaciones+testActualizarDescripcionReparacion+testActualizarFechaReparacion+testCrearCocheConMatriculaUnica+testCrearMecanicoNuevo+testCrearReparacionVariasHorasYPrecio+testActualizarTodosLosCamposReparacion+testCantidadCochesDespuesDeAdd+testCantidadMecanicosDespuesDeAdd+testCantidadReparacionesDespuesDeAdd"
```

### Listado de tests

#### Tests unitarios (13 tests)
| Test | Descripción |
|------|-------------|
| `contextLoads` | Verifica que el contexto de Spring Boot carga correctamente |
| `testCantidadCoches` | Verifica que se insertan 10 coches desde los fixtures |
| `testCocheNoNull` | Verifica que ningún coche es null |
| `testIdsCoche` | Verifica que todos los coches tienen ID asignado |
| `testMatriculasUnicas` | Verifica que las matrículas son únicas |
| `testAddCar` | Verifica la creación de un coche nuevo |
| `testUpdateCar` | Verifica la actualización de marca y modelo de un coche |
| `testDeleteCar` | Verifica la eliminación de un coche |
| `testAddMecanico` | Verifica la creación de un mecánico nuevo |
| `testUpdateMecanico` | Verifica la actualización del nombre de un mecánico |
| `testDeleteMecanico` | Verifica la eliminación de un mecánico |
| `testCrearCocheDuplicadoMatricula` | Verifica que no se permite duplicar matrículas |
| `testCrearMecanicoDuplicadoNombre` | Verifica que se permite crear mecánicos con el mismo nombre |

#### Tests de integración (7 tests)
| Test | Descripción |
|------|-------------|
| `testRelacionCocheReparacion` | Verifica que cada reparación tiene coche y mecánico asociados |
| `testRelacionCocheMultipleReparaciones` | Verifica que un coche puede tener múltiples reparaciones |
| `testAddReparacion` | Verifica la creación de una reparación con coche y mecánico existentes |
| `testActualizarReparacion` | Verifica la actualización de descripción, horas y precio de una reparación |
| `testEliminarReparacion` | Verifica la eliminación de una reparación |
| `testCrearReparacionSinCoche` | Verifica que no se permite crear una reparación sin coche |
| `testCrearReparacionSinMecanico` | Verifica que no se permite crear una reparación sin mecánico |

#### Tests de aceptación (12 tests)
| Test | Descripción |
|------|-------------|
| `testCrearVariasReparaciones` | Verifica la creación de 5 reparaciones para el mismo coche y mecánico |
| `testEliminarCocheConReparaciones` | Verifica la eliminación de un coche después de eliminar sus reparaciones |
| `testEliminarMecanicoConReparaciones` | Verifica la eliminación de un mecánico después de eliminar sus reparaciones |
| `testActualizarDescripcionReparacion` | Verifica la actualización de la descripción de una reparación |
| `testActualizarFechaReparacion` | Verifica la actualización de la fecha de una reparación |
| `testCrearCocheConMatriculaUnica` | Verifica la creación de un coche con matrícula única |
| `testCrearMecanicoNuevo` | Verifica la creación de un mecánico nuevo |
| `testCrearReparacionVariasHorasYPrecio` | Verifica que se guardan correctamente horas y precio |
| `testActualizarTodosLosCamposReparacion` | Verifica la actualización completa de todos los campos |
| `testCantidadCochesDespuesDeAdd` | Verifica que el total de coches incrementa tras añadir uno |
| `testCantidadMecanicosDespuesDeAdd` | Verifica que el total de mecánicos incrementa tras añadir uno |
| `testCantidadReparacionesDespuesDeAdd` | Verifica que el total de reparaciones incrementa tras añadir una |

## Verificación de endpoints con curl

Una vez la aplicación esté en marcha (`docker compose up -d`), se pueden verificar los endpoints con los siguientes comandos:

### Coches

```bash
# Obtener todos los coches
curl -s http://localhost:8080/coches | python3 -m json.tool

# Obtener un coche por ID
curl -s http://localhost:8080/coches/1 | python3 -m json.tool

# Obtener un coche por matrícula
curl -s http://localhost:8080/coches/matricula/1111AAA | python3 -m json.tool

# Crear un coche nuevo
curl -s -X POST http://localhost:8080/coches \
  -H "Content-Type: application/json" \
  -d '{"matricula":"TEST123","marca":"Tesla","modelo":"Model 3"}' | python3 -m json.tool
```

### Mecánicos

```bash
# Obtener todos los mecánicos
curl -s http://localhost:8080/mecanicos | python3 -m json.tool

# Obtener un mecánico por ID
curl -s http://localhost:8080/mecanicos/1 | python3 -m json.tool

# Crear un mecánico nuevo
curl -s -X POST http://localhost:8080/mecanicos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pedro","especialidad":"Electricidad"}' | python3 -m json.tool
```

### Reparaciones

```bash
# Obtener todas las reparaciones
curl -s http://localhost:8080/reparaciones | python3 -m json.tool

# Obtener una reparación por ID
curl -s http://localhost:8080/reparaciones/1 | python3 -m json.tool

# Obtener reparaciones por coche
curl -s http://localhost:8080/reparaciones/coche/1 | python3 -m json.tool

# Obtener reparaciones por mecánico
curl -s http://localhost:8080/reparaciones/mecanico/1 | python3 -m json.tool

# Crear una reparación nueva
curl -s -X POST http://localhost:8080/reparaciones \
  -H "Content-Type: application/json" \
  -d '{"coche":{"id":1},"mecanico":{"id":1},"fecha":"2025-01-15","descripcion":"Cambio de aceite","horas":2,"precio":75.0}' | python3 -m json.tool
```

## Base de datos
- La base de datos utilizada para esta práctica ha sido MariaDB. En Docker Compose se configura como servicio:
    - Se despliega un contenedor MariaDB 11.1.
    - La aplicación se conecta a la base de datos haciendo uso de las variables de entorno.
    - El esquema se genera automáticamente mediante Hibernate (`ddl-auto=update`).

- Para los tests se utiliza **H2** (base de datos en memoria), lo que permite ejecutar los tests sin necesidad de tener MariaDB disponible.

## Pipeline CI/CD
- El pipeline se ejecuta automáticamente en cada **push** o **pull request** a la rama **main**, donde se realizan los siguientes pasos:

### Descripción del workflow (`.github/workflows/ci.yml`)

| Paso | Descripción |
|------|-------------|
| **Checkout code** | Descarga el código del repositorio |
| **Set up JDK 17** | Configura el entorno Java con JDK 17 (Temurin) |
| **Checkstyle linting** | Valida el formato y la correctesa del código con Checkstyle |
| **Run unit tests** | Ejecuta los 13 tests unitarios (contexto, CRUD de coches y mecánicos) |
| **Run integration tests** | Ejecuta los 7 tests de integración (relaciones entre entidades) |
| **Run acceptance tests** | Ejecuta los 12 tests de aceptación (operaciones complejas) |
| **Package application** | Empaqueta la aplicación como WAR con Maven |
| **Build Docker image** | Construye la imagen Docker de la aplicación |

- Los tests utilizan una base de datos H2 en memoria, configurada en `application-test.properties`, lo que elimina la necesidad de un servicio MariaDB en el pipeline.

## Construcción de la imagen Docker
- La aplicación se empaqueta como un **WAR** y se despliega en un contenedor **Tomcat**. Para construir la imagen de manera manual, hacemos uso del siguiente comando:
```bash
cd taller/taller
mvn clean package -DskipTests
docker build -t taller-app .
```

## Checkstyle
- El proyecto incluye un archivo `checkstyle.xml` que define las reglas de estilo del código. Se verifica con:
```bash
cd taller/taller
mvn checkstyle:check
```

## Conclusión
- Con esta práctica hemos conseguido:
    - Integrar una aplicación Spring-Boot con Docker.
    - Automatizar la ejecución de tests mediante CI/CD.
    - Validar la calidad del código con Checkstyle.
    - Separar los tests en unitarios, de integración y de aceptación.
    - Garantizamos que el código es de calidad antes de hacer la imagen Docker de la aplicación.