# Capital Humano

Sistema integral de gestión de recursos humanos y evaluación de competencias de candidatos mediante cuestionarios adaptativos e inteligencia artificial.

---

## Estructura del Proyecto

```text
capitalhumano/
├── backend/            # API REST con Spring Boot 4.1.0 & Java 21
│   ├── src/            # Controladores, Servicios, Seguridad, JPA
│   └── pom.xml         # Dependencias de Maven
├── frontend/           # SPA con React 19 & Vite
│   ├── src/            # Vistas, Servicios, Contextos y Componentes
│   └── package.json    # Dependencias de Node.js
├── init/               # Scripts de inicialización de contenedores
│   ├── sql/            # Script inicial de base de datos PostgreSQL (01-init.sql)
│   └── ldap/           # Datos LDIF de usuarios consultores (consultores.ldif)
└── compose.yaml        # Servicios Docker (PostgreSQL, OpenLDAP, phpLDAPadmin)
```

---

## Requisitos Previos

- Docker y Docker Compose
- Java 21 (JDK)
- Node.js 20.19+ o 22.12+ y npm

---

## Puesta en Marcha

### 1. Configurar variables de entorno

Desde la raíz del repositorio:

```bash
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

En `backend/.env`, reemplazá `tu_api_key_aqui` por tu API Key de Google Gemini.

### 2. Iniciar Base de Datos y OpenLDAP

Desde la raíz del repositorio:

```bash
docker compose up -d
```

Servicios disponibles:
- PostgreSQL: `localhost:5432` (Base de datos: `tm_database` | Usuario: `admin` | Clave: `admin`)
- OpenLDAP: `localhost:389`
- phpLDAPadmin: `http://localhost:8081` (Login DN: `cn=admin,dc=capitalhumano,dc=com` | Clave: `admin`)

---

### 3. Iniciar el Backend

```bash
cd backend
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

---

### 4. Iniciar el Frontend

```bash
cd frontend
npm install
npm run dev
```

La aplicación web estará disponible en `http://localhost:3000`.

---

## Usuarios de Prueba

- **Consultores (LDAP):**
  - Usuario: `mfrank` | Contraseña: `1234`
  - Usuario: `jperez` | Contraseña: `1234`
- **Candidatos:**
  - Acceso mediante código único generado al crear una evaluación desde el panel de consultor.
