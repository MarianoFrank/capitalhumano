# Capital Humano

Sistema integral de gestión de recursos humanos y evaluación de competencias de candidatos mediante cuestionarios adaptativos e inteligencia artificial.

---

## 📁 Estructura del Proyecto

```text
capitalhumano/
├── backend/            # API REST con Spring Boot 3 & Java 21
│   ├── src/
│   │   ├── main/java/  # Controladores, Servicios, Repositorios, Seguridad
│   │   └── resources/ # Configuración (application.properties)
│   └── pom.xml
├── frontend/           # SPA con React & Vite
│   ├── src/
│   │   ├── components/ # Componentes reutilizables
│   │   ├── pages/      # Vistas (Consultor y Candidato)
│   │   └── config/     # Configuración de cliente HTTP (Axios)
│   └── package.json
├── compose.yaml        # Servicios Docker (PostgreSQL, OpenLDAP, phpLDAPadmin)
├── sql-init/           # Scripts SQL de inicialización de base de datos
└── ldap-init/          # Datos LDIF de inicialización de usuarios consultores
```

---

## 🛠️ Tecnologías

* **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA, JWT, LDAP, Google Gemini API.
* **Frontend:** React 19, Vite, Tailwind CSS, PrimeReact, Axios.
* **Base de Datos:** PostgreSQL 16.
* **Directorio Corporativo:** OpenLDAP.
* **Infraestructura:** Docker Compose.

---

## 🚀 Puesta en Marcha

### 1. Iniciar Base de Datos y LDAP
Desde la raíz del proyecto, levantá los contenedores Docker:
```bash
docker compose up -d
```

Servicios levantados:
* **PostgreSQL:** `localhost:5432` (`tm_database` / user: `admin` / pass: `admin`)
* **OpenLDAP:** `localhost:389`
* **phpLDAPadmin:** `http://localhost:8081` (`cn=admin,dc=capitalhumano,dc=com` / pass: `admin`)

---

### 2. Iniciar el Backend
```bash
cd backend
./mvnw spring-boot:run
```
La API estará disponible en `http://localhost:8080`.

---

### 3. Iniciar el Frontend
```bash
cd frontend
npm install
npm run dev
```
La aplicación web estará disponible en `http://localhost:3000` (o el puerto indicado por Vite).

---

## 🔐 Usuarios y Accesos de Prueba

* **Consultores (LDAP):**
  * Usuario: `mfrank` | Contraseña: `1234`
  * Usuario: `jperez` | Contraseña: `1234`
* **Candidatos:**
  * Ingreso mediante clave de acceso única de 8 caracteres generada al crear una evaluación.
