# Arquitectura del Frontend - Capital Humano

Estructura modular orientada a dominios (**Feature-Based Architecture**) con separación clara de capas.

---

## Estructura de Directorios (`src/`)

```
src/
├── api.js                # Cliente Axios configurado con interceptores de sesión
├── constants.js          # Constantes de negocio (roles, estados, tipos de pregunta)
├── utils.js              # Funciones auxiliares puras (exportación Excel, formato fechas)
├── App.jsx               # Enrutador principal declarativo de la aplicación
├── main.jsx              # Punto de entrada de React con proveedores globales
├── index.css             # Estilos globales y reglas de maquetado
│
├── components/           # Componentes comunes, layouts y guardias de navegación
│   ├── BarraLateral.jsx      # Menú de navegación lateral del consultor
│   ├── Cargando.jsx          # Spinner de carga de pantalla completa
│   ├── DisenioCandidato.jsx  # Layout contenedor para el flujo del candidato
│   ├── DisenioConsultor.jsx  # Layout contenedor con scroll y barra lateral
│   └── RutaProtegida.jsx     # Guard de verificación de autenticación y rol
│
├── context/              # Estado global de la aplicación
│   ├── AuthContext.jsx       # Sesión, usuario activo y hook useAuth
│   ├── TemaContext.jsx       # Tema claro/oscuro y hook useTema
│   ├── ToastContext.jsx      # Mensajes toast y hook useToast
│   └── index.js              # ProveedorApp y exportación unificada de hooks
│
├── hooks/                # Custom hooks reutilizables
│   └── usePaginacion.js      # Control de paginación y orden para tablas PrimeReact
│
├── services/             # Capa exclusiva de comunicación HTTP con la API Backend
│   ├── auth.js               # Login consultor, login candidato, perfil y logout
│   ├── candidatos.js         # Listado paginado y carga masiva por CSV
│   ├── cuestionarios.js      # Inicio, carga de bloques y envío de respuestas
│   ├── evaluaciones.js       # Generación de evaluaciones y asignación de claves
│   ├── preguntas.js          # Banco de preguntas y generación asistida por IA
│   ├── puestos.js            # Select de puestos y validación de competencias
│   └── reportes.js           # Puestos, evaluaciones y orden de mérito
│
└── views/                # Pantallas y subcomponentes organizados por dominio
    ├── auth/                 # Vistas de acceso y errores
    │   ├── IniciarSesion.jsx     # Login para consultores
    │   ├── InicioPublico.jsx     # Ingreso para candidatos con código de acceso
    │   └── NoEncontrado.jsx      # Pantalla de error 404
    │
    ├── consultor/            # Vistas del panel de administración
    │   ├── BienvenidaConsultor.jsx # Home del consultor con accesos rápidos
    │   ├── evaluar/                # Generación de evaluaciones y selección de candidatos
    │   ├── preguntas/              # Banco de preguntas y creación con IA
    │   └── reportes/               # Reportes de orden de mérito y descarga de PDF
    │
    └── candidato/            # Flujo de examen para el postulante
        ├── Instrucciones.jsx        # Reglas previas a iniciar
        ├── AsistenteCuestionario.jsx# Cuestionario unidireccional en bloques
        ├── BloquePreguntas.jsx      # Renderizado de preguntas únicas/múltiples
        ├── Temporizador.jsx         # Contador regresivo con alerta crítica
        └── EvaluacionCompletada.jsx # Pantalla de confirmación y finalización
```

---

## Función Arquitectónica de Cada Capa

- **`api.js`**: Única instancia de comunicación HTTP (Axios). Maneja `baseURL`, cookies de sesión e intercepta respuestas 401 para expiración de credenciales.
- **`constants.js`**: Fuente única de verdad para valores fijos del sistema (evita strings mágicos repetidos).
- **`utils.js`**: Funciones puras independientes de la UI (sin dependencias de estado de React).
- **`services/`**: Desacopla la lógica de red de las vistas. Si un endpoint o parámetro cambia en el backend, solo se edita el archivo correspondiente en esta carpeta.
- **`context/`**: Manejo de estado compartido entre componentes lejanos (usuario actual, tema visual y notificaciones emergentes).
- **`hooks/`**: Lógica de estado reutilizable en múltiples pantallas (ej. paginación de tablas de PrimeReact).
- **`components/`**: Componentes visuales genéricos que estructuran la aplicación (layouts y protección de rutas).
- **`views/`**: Componentes de página que orquestan los servicios, hooks y subcomponentes específicos de cada caso de uso.
- **`App.jsx`**: Mapa centralizado de navegación. Define qué vista responde a cada ruta y cuáles requieren permisos de consultor.
