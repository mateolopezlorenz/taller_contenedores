# Práctica Docker - Fase 3: Pipeline CI/CD

## Descripción

Se ha realizado la práctica del módulo **Despliegue y administración de contenedores**, cuyo objetivo es implementar una Pipeline CI/CD para una aplicación Java haciendo uso de **Docker** y **GitHub Actions**. La aplicación que ha sido desarrollada consta de una API REST basada en un **taller de coches**, donde se gestionan las siguientes entidades:

- **Coche** (`coche`): matrícula, marca, modelo.
- **Mecánico** (`mecanico`): nombre, especialidad.
- **Reparación** (`reparacion`): coche, mecánico, fecha, descripción, horas, precio.

### Estructura del proyecto

```
taller/taller/
├── pom.xml                         # Configuración Maven con Spring Boot 4.0.2
├── Dockerfile                      # Imagen Docker (Tomcat + WAR)
├── docker-compose.yaml             # Orquestación: App + MariaDB
├── checkstyle.xml                  # Configuración de linting (Checkstyle)
├── src/
│   ├── main/
│   │   ├── java/mateolopez/taller/
│   │   │   ├── model/              # Entidades JPA (Coche, Mecanico, Reparacion)
│   │   │   ├── repository/         # Repositorios Spring Data JPA
│   │   │   ├── service/            # Servicios de negocio
│   │   │   ├── controller/         # Controladores REST (endpoints JSON)
│   │   │   └── TallerApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/mateolopez/taller/
│       │   ├── Tests.java          # 31 tests (unitarios, integración, aceptación)
│       │   ├── TallerApplicationTests.java
│       │   └── fixtures/DataFixtures.java  # Datos de prueba
│       └── resources/
│           └── application-test.properties # Config H2 para tests
└── .github/workflows/
    └── ci.yml                      # Pipeline CI/CD con GitHub Actions
```

## Puesta en marcha

### Requisitos previos
- Docker y Docker Compose
- Java 17 (JDK)
- Maven 3.9+

### Iniciar la aplicación con Docker Compose

```bash
cd taller/taller
mvn clean package -DskipTests
docker compose up -d --build
```

Esto levanta:
- **MariaDB** en el puerto 3306 con la base de datos `tallerdb`.
- **Aplicación Tomcat** en el puerto 8080.

Los datos de prueba (fixtures) se cargan automáticamente al arrancar la aplicación a través de la clase `DataFixtures`, que inserta:
- 5 mecánicos
- 10 coches
- 10 reparaciones

### Parar la aplicación

```bash
docker compose down -v
```

## Ejecución de tests

Los tests se ejecutan con H2 en memoria (perfil `test`), sin necesidad de base de datos externa:

```bash
cd taller/taller
mvn clean test
```

### Ejecución del linting (Checkstyle)

```bash
cd taller/taller
mvn checkstyle:check
```

## Endpoints de la API

Todos los endpoints devuelven **JSON**.

### Coches

| Método | Endpoint                      | Descripción                    |
|--------|-------------------------------|--------------------------------|
| GET    | `/coches`                     | Listar todos los coches        |
| GET    | `/coches/{id}`                | Obtener coche por ID           |
| GET    | `/coches/matricula/{matricula}` | Obtener coche por matrícula  |
| POST   | `/coches`                     | Crear un nuevo coche           |

### Mecánicos

| Método | Endpoint            | Descripción                       |
|--------|---------------------|-----------------------------------|
| GET    | `/mecanicos`        | Listar todos los mecánicos        |
| GET    | `/mecanicos/{id}`   | Obtener mecánico por ID           |
| POST   | `/mecanicos`        | Crear un nuevo mecánico           |

### Reparaciones

| Método | Endpoint                       | Descripción                             |
|--------|--------------------------------|-----------------------------------------|
| GET    | `/reparaciones`                | Listar todas las reparaciones           |
| GET    | `/reparaciones/{id}`           | Obtener reparación por ID               |
| GET    | `/reparaciones/coche/{id}`     | Reparaciones por coche                  |
| GET    | `/reparaciones/mecanico/{id}`  | Reparaciones por mecánico               |
| POST   | `/reparaciones`                | Crear una nueva reparación              |

### Ejemplos con curl

```bash
# --- Coches ---

# Listar todos los coches
curl -s http://localhost:8080/coches | jq

# Obtener un coche por ID
curl -s http://localhost:8080/coches/1 | jq

# Obtener un coche por matrícula
curl -s http://localhost:8080/coches/matricula/1111AAA | jq

# Crear un nuevo coche
curl -s -X POST http://localhost:8080/coches \
  -H "Content-Type: application/json" \
  -d '{"matricula":"NEW1234","marca":"Tesla","modelo":"Model 3"}' | jq

# --- Mecánicos ---

# Listar todos los mecánicos
curl -s http://localhost:8080/mecanicos | jq

# Obtener un mecánico por ID
curl -s http://localhost:8080/mecanicos/1 | jq

# Crear un nuevo mecánico
curl -s -X POST http://localhost:8080/mecanicos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pedro Sánchez","especialidad":"Electricidad"}' | jq

# --- Reparaciones ---

# Listar todas las reparaciones
curl -s http://localhost:8080/reparaciones | jq

# Obtener una reparación por ID
curl -s http://localhost:8080/reparaciones/1 | jq

# Reparaciones de un coche concreto
curl -s http://localhost:8080/reparaciones/coche/1 | jq

# Reparaciones de un mecánico concreto
curl -s http://localhost:8080/reparaciones/mecanico/1 | jq

# Crear una nueva reparación (usando IDs de coche y mecánico existentes)
curl -s -X POST http://localhost:8080/reparaciones \
  -H "Content-Type: application/json" \
  -d '{"coche":{"id":1},"mecanico":{"id":1},"fecha":"2025-06-15","descripcion":"Cambio de aceite","horas":2,"precio":120.0}' | jq
```

## Tests

El proyecto incluye **32 tests** organizados en tres categorías:

### Tests unitarios (11 tests)
Verifican el funcionamiento básico de las entidades y repositorios:
- `testCantidadCoches` - Verifica que hay 10 coches cargados por los fixtures.
- `testCocheNoNull` - Comprueba que ningún coche es null.
- `testIdsCoche` - Verifica que todos los coches tienen ID asignado.
- `testMatriculasUnicas` - Comprueba que las matrículas son únicas.
- `testAddCar` - Insertar un coche nuevo.
- `testUpdateCar` - Actualizar marca y modelo de un coche.
- `testDeleteCar` - Eliminar un coche.
- `testAddMecanico` - Insertar un mecánico nuevo.
- `testUpdateMecanico` - Actualizar el nombre de un mecánico.
- `testDeleteMecanico` - Eliminar un mecánico.
- `testCrearCocheDuplicadoMatricula` - Verifica error al duplicar matrícula.

### Tests de integración (7 tests)
Comprueban las relaciones entre entidades y la integridad de la base de datos:
- `testCrearMecanicoDuplicadoNombre` - Permite mecánicos con mismo nombre.
- `testRelacionCocheReparacion` - Verifica relaciones coche-mecánico en reparaciones.
- `testRelacionCocheMultipleReparaciones` - Un coche puede tener múltiples reparaciones.
- `testAddReparacion` - Crear una reparación con coche y mecánico existentes.
- `testActualizarReparacion` - Actualizar campos de una reparación.
- `testEliminarReparacion` - Eliminar una reparación.
- `testCrearReparacionSinCoche` - Error al crear reparación sin coche.
- `testCrearReparacionSinMecanico` - Error al crear reparación sin mecánico.

### Tests de aceptación (14 tests)
Validan escenarios completos de uso:
- `testCrearVariasReparaciones` - Crear 5 reparaciones en lote.
- `testEliminarCocheConReparaciones` - Eliminar coche tras borrar sus reparaciones.
- `testEliminarMecanicoConReparaciones` - Eliminar mecánico tras borrar sus reparaciones.
- `testActualizarDescripcionReparacion` - Actualizar solo la descripción.
- `testActualizarFechaReparacion` - Actualizar solo la fecha.
- `testCrearCocheConMatriculaUnica` - Crear coche con matrícula nueva.
- `testCrearMecanicoNuevo` - Crear mecánico nuevo.
- `testCrearReparacionVariasHorasYPrecio` - Verificar horas y precio.
- `testActualizarTodosLosCamposReparacion` - Actualizar todos los campos.
- `testCantidadCochesDespuesDeAdd` - Verificar incremento de coches.
- `testCantidadMecanicosDespuesDeAdd` - Verificar incremento de mecánicos.
- `testCantidadReparacionesDespuesDeAdd` - Verificar incremento de reparaciones.
- `contextLoads` - Verifica que el contexto de Spring arranca correctamente.

## Fixtures (Datos de prueba)

La clase `DataFixtures` carga automáticamente datos al arrancar:

- **5 mecánicos**: Juan Pérez, Ana Gómez, Luis Martínez, María López, Carlos Fernández.
- **10 coches**: Toyota Corolla, Ford Fiesta, Honda Civic, BMW Serie 3, Audi A4, Mercedes C200, Kia Ceed, Nissan Leaf, Seat Ibiza, Volkswagen Golf.
- **10 reparaciones**: Una por cada coche, asignada a mecánicos de forma rotatoria.

## Dockerfile

El Dockerfile despliega la aplicación WAR sobre **Tomcat 10.1** con JDK 17:
1. Usa `tomcat:10.1.13-jdk17` como imagen base.
2. Elimina las aplicaciones por defecto de Tomcat.
3. Copia el WAR generado por Maven como `ROOT.war`.
4. Expone el puerto 8080.

```bash
# Construir la imagen manualmente
mvn clean package -DskipTests
docker build -t taller-app .
```

## Docker Compose

El `docker-compose.yaml` orquesta dos servicios:
- **db**: MariaDB 11.1 con la base de datos `tallerdb`.
- **app**: La aplicación Spring Boot sobre Tomcat, conectada a la base de datos.

Variables de entorno configuradas automáticamente para la conexión.

## Pipeline CI/CD (GitHub Actions)

El fichero `.github/workflows/ci.yml` define un pipeline con 4 jobs que se ejecutan en cada **push** o **pull request** a `main`:

### 1. `lint` - Checkstyle Linting
- Configura JDK 17.
- Ejecuta `mvn checkstyle:check` para validar el formato y la correcteza del código.

### 2. `unit-tests` - Tests unitarios e integración
- Depende del paso `lint`.
- Ejecuta `mvn clean test` usando H2 en memoria (sin necesidad de base de datos externa).
- Ejecuta los 32 tests (unitarios, integración y aceptación con JUnit).

### 3. `docker-build` - Construcción de la imagen Docker
- Depende del paso `unit-tests`.
- Empaqueta la aplicación como WAR.
- Construye la imagen Docker.

### 4. `acceptance-tests` - Tests de aceptación con Docker Compose
- Depende del paso `docker-build`.
- Levanta la aplicación completa con Docker Compose (App + MariaDB).
- Ejecuta tests de aceptación contra los endpoints reales usando `curl`.
- Verifica los endpoints GET y POST principales.
- Limpia los contenedores al finalizar.

## Pasos de verificación

A continuación se detallan los pasos para comprobar que todo funciona correctamente, tanto en local como en el pipeline CI/CD.

### Paso 1: Verificar el linting (Checkstyle)

Comprueba que el código cumple con las reglas de estilo definidas en `checkstyle.xml`:

```bash
cd taller/taller
mvn checkstyle:check
```

✅ **Resultado esperado:** `BUILD SUCCESS` sin errores de estilo.

### Paso 2: Ejecutar los tests (unitarios, integración y aceptación)

Los tests se ejecutan con una base de datos H2 en memoria, sin necesidad de MariaDB ni Docker:

```bash
cd taller/taller
mvn clean test
```

✅ **Resultado esperado:** `BUILD SUCCESS` con todos los tests pasados (ver sección [Tests](#tests) para el detalle de los 32 tests).

### Paso 3: Construir el WAR y la imagen Docker

```bash
cd taller/taller
mvn clean package -DskipTests
docker build -t taller-app .
```

✅ **Resultado esperado:** La imagen Docker `taller-app` se construye sin errores.

### Paso 4: Levantar la aplicación con Docker Compose

```bash
cd taller/taller
docker compose up -d --build
```

Espera unos segundos a que la aplicación arranque. Puedes comprobar el estado de los contenedores con:

```bash
docker compose ps
```

✅ **Resultado esperado:** Los dos servicios (`db` y `app`) aparecen con estado `Up` / `running`.

### Paso 5: Verificar los endpoints de la API

Una vez la aplicación esté arrancada, comprueba los endpoints principales:

#### 5.1 Listar coches

```bash
curl -s http://localhost:8080/coches | jq
```

✅ **Resultado esperado:** Un array JSON con 10 coches (cargados por los fixtures).

#### 5.2 Obtener un coche por ID

```bash
curl -s http://localhost:8080/coches/1 | jq
```

✅ **Resultado esperado:** Un objeto JSON con los datos del coche con ID 1.

#### 5.3 Obtener un coche por matrícula

```bash
curl -s http://localhost:8080/coches/matricula/1111AAA | jq
```

✅ **Resultado esperado:** Un objeto JSON con el coche cuya matrícula es `1111AAA`.

#### 5.4 Crear un coche nuevo

```bash
curl -s -X POST http://localhost:8080/coches \
  -H "Content-Type: application/json" \
  -d '{"matricula":"NEW1234","marca":"Tesla","modelo":"Model 3"}' | jq
```

✅ **Resultado esperado:** Un objeto JSON con el coche creado, incluyendo un `id` asignado.

#### 5.5 Listar mecánicos

```bash
curl -s http://localhost:8080/mecanicos | jq
```

✅ **Resultado esperado:** Un array JSON con 5 mecánicos.

#### 5.6 Obtener un mecánico por ID

```bash
curl -s http://localhost:8080/mecanicos/1 | jq
```

✅ **Resultado esperado:** Un objeto JSON con los datos del mecánico con ID 1.

#### 5.7 Listar reparaciones

```bash
curl -s http://localhost:8080/reparaciones | jq
```

✅ **Resultado esperado:** Un array JSON con 10 reparaciones, cada una con su coche y mecánico asociados.

#### 5.8 Reparaciones por coche

```bash
curl -s http://localhost:8080/reparaciones/coche/1 | jq
```

✅ **Resultado esperado:** Un array JSON con las reparaciones asociadas al coche con ID 1.

#### 5.9 Reparaciones por mecánico

```bash
curl -s http://localhost:8080/reparaciones/mecanico/1 | jq
```

✅ **Resultado esperado:** Un array JSON con las reparaciones del mecánico con ID 1.

### Paso 6: Parar la aplicación

```bash
cd taller/taller
docker compose down -v
```

✅ **Resultado esperado:** Todos los contenedores se detienen y los volúmenes se eliminan.

### Paso 7: Verificar el pipeline CI/CD

Al hacer **push** o abrir un **pull request** a la rama `main`, el pipeline de GitHub Actions ejecuta automáticamente los siguientes jobs en orden:

1. **Lint** → Checkstyle
2. **Unit Tests** → Tests con H2
3. **Docker Build** → Construcción de la imagen Docker
4. **Acceptance Tests** → Verificación de endpoints con Docker Compose

✅ **Resultado esperado:** Los 4 jobs aparecen en verde (✓) en la pestaña **Actions** del repositorio en GitHub.

## Conclusión

Con esta práctica se ha conseguido:
- Integrar una aplicación Spring Boot con Docker y Docker Compose.
- Automatizar la validación del código con Checkstyle.
- Ejecutar tests unitarios, de integración y de aceptación de forma automatizada.
- Construir imágenes Docker como parte del pipeline CI/CD.
- Garantizar la calidad del código antes de cada despliegue.