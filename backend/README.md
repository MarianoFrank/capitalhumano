# Arquitectura del Backend - Capital Humano

API REST desarrollada con **Spring Boot 4**, **Java 21** y **PostgreSQL**. La aplicación organiza la lógica por dominios y separa la exposición HTTP, la lógica de negocio, el acceso a datos y los componentes transversales.

---

## Flujo General de una Petición

```text
Cliente HTTP
    -> Controller
    -> Service
    -> Repository / Componente externo
    -> Model
    -> DTO (con métodos estáticos from())
    -> Respuesta JSON
```

Los controladores reciben las solicitudes, los servicios contienen las reglas de negocio y coordinan los repositorios, y los repositorios realizan el acceso a PostgreSQL mediante Spring Data JPA.

---

## Estructura de Directorios

```text
backend/
├── pom.xml                          # Dependencias y configuración de Maven
├── mvnw / mvnw.cmd                  # Maven Wrapper para ejecutar el proyecto
├── .env.example                     # Variables de entorno de referencia
│
└── src/
    ├── main/
    │   ├── java/ar/edu/utn/frsf/capitalhumano/
    │   │   ├── CapitalHumanoApplication.java
    │   │   ├── config/              # Configuración global de Spring
    │   │   ├── controller/          # Endpoints REST por dominio
    │   │   ├── dto/                 # Records Java con métodos from() para mapeo
    │   │   ├── event/               # Eventos internos de negocio
    │   │   ├── exception/           # Manejo centralizado de errores
    │   │   ├── model/               # Entidades JPA y enumeraciones
    │   │   ├── repository/          # Acceso a datos con Spring Data JPA y @Query
    │   │   ├── scheduler/           # Tareas periódicas programadas
    │   │   ├── security/            # Autenticación, autorización y LDAP
    │   │   └── service/             # Reglas y procesos de negocio
    │   └── resources/
    │       └── application.properties # Puerto, BD, JWT, CORS y LDAP
    │
    └── test/
        ├── java/                    # Pruebas unitarias e integradas
        └── resources/               # Configuración utilizada por las pruebas
```

---

## Función Arquitectónica de Cada Capa

### `CapitalHumanoApplication.java`

Punto de entrada de la aplicación. Configura Spring Boot y habilita seguridad a nivel de métodos, ejecución asíncrona y tareas programadas. También carga las variables del archivo `.env` del backend.

### `config/`

Contiene la configuración central de la aplicación.

- **`SecurityConfig.java`**: configura CORS, sesiones sin estado, filtros JWT, permisos por rol y autenticación de consultores contra LDAP.

### `controller/`

Expone los endpoints REST del sistema. Los archivos agrupan operaciones relacionadas con autenticación, candidatos, cuestionarios, evaluaciones, puestos, competencias, factores, preguntas, empresas, reportes e integración con IA. Estas clases reciben peticiones HTTP, validan los datos de entrada y delegan el procesamiento a los servicios.

### `dto/`

Define records Java utilizados en las solicitudes y respuestas HTTP. Cada record incluye métodos estáticos `from()` para convertir entidades JPA en DTOs, eliminando la necesidad de librerías externas de mapeo.

### `event/`

Contiene eventos internos utilizados para comunicar acciones de negocio entre componentes sin acoplarlos directamente.

- **`CuestionarioCompletadoEvent.java`**: representa la finalización de un cuestionario y permite ejecutar procesos asociados de manera desacoplada.

### `exception/`

Centraliza la traducción de excepciones a respuestas HTTP consistentes.

- **`GlobalExceptionHandler.java`**: maneja argumentos inválidos, errores de validación, conflictos de integridad, cuerpos JSON incorrectos, entidades no encontradas (404) y errores generales de ejecución. Devuelve respuestas estructuradas mediante `ApiErrorResponse`.

### `model/`

Representa el dominio persistido y sus relaciones. Incluye las entidades JPA del sistema, como candidatos, consultores, empresas, puestos, evaluaciones, cuestionarios, preguntas, opciones y puntajes. También contiene enumeraciones para roles, estados, géneros y tipos de documento, competencia y pregunta.

### `repository/`

Contiene las interfaces de Spring Data JPA. Cada repositorio administra las operaciones de persistencia de una entidad. Las consultas de filtrado dinámico se implementan con `@Query` JPQL estándar y parámetros opcionales.

### `scheduler/`

Ejecuta procesos automáticos en segundo plano.

- **`CuestionarioVencimientoScheduler.java`**: diariamente a medianoche finaliza los cuestionarios vencidos mediante `CuestionarioService`.

### `security/`

Implementa autenticación y control de acceso del backend.

- **`JwtService.java`**: genera y valida tokens JWT, extrae usuario y rol, y crea o elimina la cookie que almacena la sesión.
- **`JwtCookieFilter.java`**: lee el JWT desde la cookie, valida su vigencia y registra la autenticación y los permisos en el contexto de Spring Security.
- **`LdapService.java`**: consulta OpenLDAP para comprobar si un consultor existe y obtener sus datos de perfil.
- **`SecurityService.java`**: coordina la autenticación de consultores y candidatos, el acceso a perfiles y la verificación de propiedad de cuestionarios.

### `service/`

Es la capa principal de lógica de negocio. Los servicios coordinan repositorios y componentes externos para implementar los casos de uso del sistema. También procesan cálculos de puntajes, generación de cuestionarios y evaluaciones, carga de candidatos, generación asistida por IA, consultas de reportes y tareas asíncronas.

### `src/main/resources/`

Contiene los archivos de configuración de ejecución. `application.properties` define el puerto, la conexión a PostgreSQL, parámetros JWT, CORS, LDAP y otras propiedades necesarias para levantar el backend.

### `src/test/`

Contiene las pruebas del proyecto. Las pruebas verifican DTOs, servicios, consultas de repositorios, manejo global de excepciones y el contexto general de Spring Boot. Los recursos de prueba separan la configuración utilizada por los tests de la configuración principal.

### `pom.xml`

Define las dependencias de Spring Boot y librerías adicionales, además de los plugins necesarios para compilar y ejecutar el proyecto con Maven.
