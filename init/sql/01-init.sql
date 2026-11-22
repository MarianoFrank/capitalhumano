-- =====================================================================
-- CAPITAL HUMANO - SCRIPT DE INICIALIZACIÓN Y POBLADO DE BASE DE DATOS
-- Compatible con PostgreSQL 14+ / Spring Boot 3 & 4
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0. CREACIÓN DE TABLAS (SI NO EXISTEN)
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS empresas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    fecha_baja TIMESTAMP
);

CREATE TABLE IF NOT EXISTS puestos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(255),
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    empresa_id BIGINT REFERENCES empresas(id),
    fecha_baja TIMESTAMP
);

CREATE TABLE IF NOT EXISTS competencias (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(255) UNIQUE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(255) NOT NULL, -- 'CONDUCTUAL', 'TECNICA'
    fecha_baja TIMESTAMP
);

CREATE TABLE IF NOT EXISTS factores (
    id BIGSERIAL PRIMARY KEY,
    competencia_id BIGINT NOT NULL REFERENCES competencias(id),
    codigo VARCHAR(255) UNIQUE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    numero_orden INT NOT NULL,
    fecha_baja TIMESTAMP
);

CREATE TABLE IF NOT EXISTS preguntas (
    id BIGSERIAL PRIMARY KEY,
    factor_id BIGINT NOT NULL REFERENCES factores(id),
    version INT NOT NULL DEFAULT 1,
    nombre VARCHAR(255) NOT NULL,
    texto TEXT NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(255) NOT NULL, -- 'SINGLE_CHOICE', 'MULTIPLE_CHOICE'
    fecha_modificacion TIMESTAMP,
    fecha_baja TIMESTAMP
);

CREATE TABLE IF NOT EXISTS opciones (
    id BIGSERIAL PRIMARY KEY,
    pregunta_id BIGINT NOT NULL REFERENCES preguntas(id),
    orden_visualizacion INT NOT NULL,
    ponderacion INT NOT NULL,
    texto TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS puesto_competencias (
    puesto_id BIGINT NOT NULL REFERENCES puestos(id),
    competencia_id BIGINT NOT NULL REFERENCES competencias(id),
    ponderacion_requerida INT,
    PRIMARY KEY (puesto_id, competencia_id)
);

CREATE TABLE IF NOT EXISTS candidatos (
    id BIGSERIAL PRIMARY KEY,
    numero_candidato BIGINT UNIQUE NOT NULL,
    tipo_documento VARCHAR(255) NOT NULL, -- 'DNI', 'LE', 'LC', 'PP'
    numero_documento VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    genero VARCHAR(255) NOT NULL, -- 'H', 'M'
    email VARCHAR(255) NOT NULL,
    escolaridad VARCHAR(255),
    nacionalidad VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS consultores (
    id SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS evaluaciones (
    id BIGSERIAL PRIMARY KEY,
    consultor_id INT NOT NULL REFERENCES consultores(id),
    puesto_id BIGINT NOT NULL REFERENCES puestos(id),
    codigo VARCHAR(255) UNIQUE NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_cierre TIMESTAMP NOT NULL,
    duracion INT NOT NULL
);

CREATE TABLE IF NOT EXISTS cuestionarios (
    id BIGSERIAL PRIMARY KEY,
    evaluacion_id BIGINT NOT NULL REFERENCES evaluaciones(id),
    candidato_id BIGINT NOT NULL REFERENCES candidatos(id),
    clave_acceso VARCHAR(255) UNIQUE NOT NULL,
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    ultimo_acceso TIMESTAMP,
    cantidad_accesos INT NOT NULL DEFAULT 0,
    puntaje_total DOUBLE PRECISION,
    estado VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS bloques (
    id BIGSERIAL PRIMARY KEY,
    cuestionario_id BIGINT NOT NULL REFERENCES cuestionarios(id),
    numero_bloque INT NOT NULL
);

CREATE TABLE IF NOT EXISTS items_pregunta (
    id BIGSERIAL PRIMARY KEY,
    bloque_id BIGINT NOT NULL REFERENCES bloques(id),
    pregunta_id BIGINT NOT NULL REFERENCES preguntas(id),
    orden_visualizacion INT NOT NULL,
    puntaje_obtenido DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS items_opcion (
    id BIGSERIAL PRIMARY KEY,
    item_pregunta_id BIGINT NOT NULL REFERENCES items_pregunta(id),
    opcion_id BIGINT NOT NULL REFERENCES opciones(id),
    esta_respondida BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS puntajes_competencia (
    id BIGSERIAL PRIMARY KEY,
    cuestionario_id BIGINT NOT NULL REFERENCES cuestionarios(id),
    competencia_id BIGINT NOT NULL REFERENCES competencias(id),
    puntaje DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS puntajes_factor (
    id BIGSERIAL PRIMARY KEY,
    puntaje_competencia_id BIGINT NOT NULL REFERENCES puntajes_competencia(id),
    factor_id BIGINT NOT NULL REFERENCES factores(id),
    puntaje DOUBLE PRECISION NOT NULL
);

-- ---------------------------------------------------------------------
-- 1. LIMPIEZA DE DATOS PREVIOS
-- ---------------------------------------------------------------------
TRUNCATE TABLE 
    puntajes_factor,
    puntajes_competencia,
    items_opcion,
    items_pregunta,
    bloques,
    cuestionarios,
    evaluaciones,
    candidatos,
    consultores,
    puesto_competencias,
    opciones,
    preguntas,
    factores,
    competencias,
    puestos,
    empresas
RESTART IDENTITY CASCADE;

-- ---------------------------------------------------------------------
-- 2. EMPRESAS (10)
-- ---------------------------------------------------------------------
INSERT INTO empresas (id, nombre, fecha_baja) VALUES
(1, 'TechCorp Argentina', NULL),
(2, 'Global Solutions S.A.', NULL),
(3, 'Innovate Software', NULL),
(4, 'Data Insights Corp', NULL),
(5, 'Fintech Latam', NULL),
(6, 'CyberShield Security', NULL),
(7, 'CloudOps Studio', NULL),
(8, 'E-Commerce Nexus', NULL),
(9, 'AI Frontiers', NULL),
(10, 'Agile Consulting Services', NULL);

-- ---------------------------------------------------------------------
-- 3. PUESTOS (30)
-- ---------------------------------------------------------------------
INSERT INTO puestos (id, codigo, nombre, descripcion, fecha_baja, empresa_id) VALUES
(1, 'PUE-001', 'Analista de Datos Junior', 'Limpieza, análisis y visualización de datos de negocio.', NULL, 1),
(2, 'PUE-002', 'Líder de Equipo de Desarrollo', 'Liderazgo técnico y gestión de personas.', NULL, 1),
(3, 'PUE-003', 'Desarrollador Backend Senior', 'Arquitectura y desarrollo de servicios backend escalables.', NULL, 3),
(4, 'PUE-004', 'Ejecutivo de Cuentas Corporativas', 'Gestión comercial y relaciones con clientes clave.', NULL, 2),
(5, 'PUE-005', 'Tech Lead', 'Referente técnico y diseñador de soluciones de software.', NULL, 3),
(6, 'PUE-006', 'Product Owner', 'Priorización de backlog y definición de visión de producto.', NULL, 2),
(7, 'PUE-007', 'Scrum Master', 'Facilitador de ceremonias y metodologías ágiles.', NULL, 10),
(8, 'PUE-008', 'Ingeniero DevOps', 'Automatización de infraestructura y pipelines CI/CD.', NULL, 7),
(9, 'PUE-009', 'Ingeniero QA Automation', 'Diseño y ejecución de pruebas automatizadas.', NULL, 3),
(10, 'PUE-010', 'Diseñador UX/UI', 'Diseño de interfaces de usuario y experiencia de uso.', NULL, 8),
(11, 'PUE-011', 'Ingeniero de Ciberseguridad', 'Protección de infraestructura y análisis de vulnerabilidades.', NULL, 6),
(12, 'PUE-012', 'Ingeniero de Datos', 'Construcción y mantenimiento de pipelines de datos ETL.', NULL, 4),
(13, 'PUE-013', 'Director Financiero Tecnológico (CFO)', 'Dirección y planeamiento financiero en empresas de base tecnológica.', NULL, 5),
(14, 'PUE-014', 'Recruiter IT', 'Atracción, reclutamiento y selección de talento digital.', NULL, 10),
(15, 'PUE-015', 'Desarrollador Fullstack Junior', 'Desarrollo web inicial frontend y backend.', NULL, 1),
(16, 'PUE-016', 'Arquitecto Cloud', 'Diseño de infraestructura multi-cloud de alta disponibilidad.', NULL, 7),
(17, 'PUE-017', 'Especialista en IA / Machine Learning', 'Modelado e implementación de algoritmos predictivos y LLMs.', NULL, 9),
(18, 'PUE-018', 'Gerente de Cuentas Estratégicas (KAM)', 'Fidelización y expansión de clientes estratégicos.', NULL, 2),
(19, 'PUE-019', 'Ingeniero SRE', 'Garantía de disponibilidad y resiliencia de sistemas en producción.', NULL, 7),
(20, 'PUE-020', 'Analista Funcional', 'Traducción de requerimientos de negocio a especificaciones técnicas.', NULL, 4),
(21, 'PUE-021', 'Desarrollador Móvil iOS / Android', 'Desarrollo de aplicaciones móviles nativas e híbridas.', NULL, 8),
(22, 'PUE-022', 'Analista de Soporte Nivel 2', 'Resolución de incidentes técnicos complejos de clientes.', NULL, 2),
(23, 'PUE-023', 'Director de Operaciones (COO)', 'Supervisión de operaciones globales de la empresa.', NULL, 5),
(24, 'PUE-024', 'Consultor de Transformación Digital', 'Asesoramiento a empresas en procesos de modernización tecnológica.', NULL, 10),
(25, 'PUE-025', 'Hacker Ético / Pentester', 'Pruebas de penetración y simulaciones de ataques.', NULL, 6),
(26, 'PUE-026', 'Especialista en Marketing IT', 'Generación de contenido y posicionamiento en el sector tech.', NULL, 8),
(27, 'PUE-027', 'Administrador de Bases de Datos (DBA)', 'Administración, afinamiento y alta disponibilidad en PostgreSQL.', NULL, 4),
(28, 'PUE-028', 'Ingeniero Frontend React', 'Desarrollo de interfaces ricas y optimizadas en React.', NULL, 3),
(29, 'PUE-029', 'Especialista en Cumplimiento y Normativas', 'Garantía de normativas legales, GDPR y seguridad.', NULL, 5),
(30, 'PUE-030', 'Ingeniero MLOps', 'Puesta en producción y ciclo de vida de modelos de IA.', NULL, 9);

-- ---------------------------------------------------------------------
-- 4. COMPETENCIAS (18)
-- ---------------------------------------------------------------------
INSERT INTO competencias (id, codigo, nombre, descripcion, tipo, fecha_baja) VALUES
(1, 'LID', 'Liderazgo', 'Capacidad de orientar, inspirar y motivar equipos hacia metas comunes.', 'CONDUCTUAL', NULL),
(2, 'TEQ', 'Trabajo en Equipo', 'Colaboración efectiva y sinérgica con pares y líderes.', 'CONDUCTUAL', NULL),
(3, 'PAN', 'Pensamiento Analítico', 'Interpretación rigurosa de datos y resolución fundamentada de problemas.', 'CONDUCTUAL', NULL),
(4, 'PRG', 'Programación Backend', 'Desarrollo de código robusto, seguro y de alto rendimiento.', 'TECNICA', NULL),
(5, 'COM', 'Comunicación Efectiva', 'Transmisión clara, concisa y empática de ideas técnicas y de negocio.', 'CONDUCTUAL', NULL),
(6, 'ADA', 'Adaptabilidad y Agilidad', 'Gestión positiva del cambio y resiliencia ante entornos dinámicos.', 'CONDUCTUAL', NULL),
(7, 'ARQ', 'Arquitectura de Software', 'Diseño de sistemas distribuidos, patrones y modularidad.', 'TECNICA', NULL),
(8, 'OPS', 'DevOps & Cloud', 'Administración de infraestructura, pipelines CI/CD y nubes públicas.', 'TECNICA', NULL),
(9, 'SEC', 'Ciberseguridad', 'Prácticas de desarrollo seguro, mitigación de riesgos y auditoría.', 'TECNICA', NULL),
(10, 'BDA', 'Bases de Datos & Big Data', 'Modelado, optimización de consultas y consistencia transaccional.', 'TECNICA', NULL),
(11, 'NEG', 'Negociación Comercial', 'Cierre de acuerdos comerciales y venta consultiva.', 'CONDUCTUAL', NULL),
(12, 'GES', 'Gestión de Proyectos', 'Planificación, estimación, métricas y metodologías de entrega.', 'CONDUCTUAL', NULL),
(13, 'UXD', 'Diseño de Experiencia (UX)', 'Investigación con usuarios, prototipado y validación de interfaces.', 'TECNICA', NULL),
(14, 'IAE', 'Inteligencia Artificial', 'Desarrollo, ajuste fino de modelos e ingeniería de prompts.', 'TECNICA', NULL),
(15, 'AUT', 'Automatización de QA', 'Estrategias de testing integral y desarrollo de scripts automáticos.', 'TECNICA', NULL),
(16, 'RES', 'Resolución de Problemas', 'Capacidad de diagnóstico y depuración de incidentes complejos.', 'CONDUCTUAL', NULL),
(17, 'ORIENT', 'Orientación al Cliente', 'Enfoque centrado en agregar valor constante al usuario final.', 'CONDUCTUAL', NULL),
(18, 'INNOV', 'Innovación y Creatividad', 'Propuesta de soluciones disruptivas y enfoque experimental.', 'CONDUCTUAL', NULL);

-- ---------------------------------------------------------------------
-- 5. FACTORES (54 - Exactamente 3 por Competencia)
-- ---------------------------------------------------------------------
INSERT INTO factores (id, competencia_id, codigo, nombre, descripcion, numero_orden, fecha_baja) VALUES
(1, 1, 'LID-01', 'Toma de Decisiones', 'Criterio en momentos de incertidumbre y presión.', 1, NULL),
(2, 1, 'LID-02', 'Motivación del Equipo', 'Inspiración y contención durante entregas complejas.', 2, NULL),
(3, 1, 'LID-03', 'Delegación Efectiva', 'Asignación adecuada de responsabilidades y autonomía.', 3, NULL),
(4, 2, 'TEQ-01', 'Colaboración Activa', 'Intercambio constante y proactivo de conocimientos.', 1, NULL),
(5, 2, 'TEQ-02', 'Resolución de Conflictos', 'Mediación constructiva durante desacuerdos grupales.', 2, NULL),
(6, 2, 'TEQ-03', 'Empatía y Escucha', 'Comprensión de diferentes puntos de vista en el equipo.', 3, NULL),
(7, 3, 'PAN-01', 'Análisis Cuantitativo', 'Interpretación de métricas, indicadores y KPIs.', 1, NULL),
(8, 3, 'PAN-02', 'Descomposición de Problemas', 'División estructurada de desafíos complejos en partes manejables.', 2, NULL),
(9, 3, 'PAN-03', 'Inferencia Basada en Datos', 'Conclusiones y diagnósticos fundamentados en evidencia.', 3, NULL),
(10, 4, 'PRG-01', 'Calidad y Clean Code', 'Estándares de legibilidad, mantenibilidad y buenas prácticas.', 1, NULL),
(11, 4, 'PRG-02', 'Depuración y Diagnóstico', 'Resolución metódica y eficiente de errores y bugs.', 2, NULL),
(12, 4, 'PRG-03', 'Optimización Algorítmica', 'Eficiencia en tiempo de ejecución y consumo de memoria.', 3, NULL),
(13, 5, 'COM-01', 'Comunicación Oral', 'Claridad y síntesis al expresar ideas verbalmente.', 1, NULL),
(14, 5, 'COM-02', 'Comunicación Escrita', 'Redacción técnica clara, precisa y documentación de calidad.', 2, NULL),
(15, 5, 'COM-03', 'Asertividad', 'Expresión directa, honesta y respetuosa de posturas.', 3, NULL),
(16, 6, 'ADA-01', 'Gestión del Cambio', 'Flexibilidad ante repriorizaciones y modificaciones de alcance.', 1, NULL),
(17, 6, 'ADA-02', 'Aprendizaje Continuo', 'Capacidad autodidacta para incorporar nuevas tecnologías.', 2, NULL),
(18, 6, 'ADA-03', 'Tolerancia a la Frustración', 'Resiliencia y perseverancia ante fallos o retrasos.', 3, NULL),
(19, 7, 'ARQ-01', 'Diseño de Microservicios', 'Descomposición por dominios y bajo acoplamiento.', 1, NULL),
(20, 7, 'ARQ-02', 'Patrones de Diseño', 'Aplicación correcta de patrones GoF y arquitectura limpia.', 2, NULL),
(21, 7, 'ARQ-03', 'Escalabilidad y Rendimiento', 'Diseño para alta concurrencia y tráfico masivo.', 3, NULL),
(22, 8, 'OPS-01', 'Pipelines de CI/CD', 'Automatización de compilación, testing y despliegue continuo.', 1, NULL),
(23, 8, 'OPS-02', 'Infraestructura como Código', 'Manejo declarativo con Terraform, Ansible o CloudFormation.', 2, NULL),
(24, 8, 'OPS-03', 'Monitoreo y Alertas', 'Observabilidad de sistemas con métricas, trazas y logs.', 3, NULL),
(25, 9, 'SEC-01', 'Análisis de Vulnerabilidades', 'Identificación temprana de brechas de seguridad.', 1, NULL),
(26, 9, 'SEC-02', 'OWASP y Código Seguro', 'Mitigación de ataques comunes (SQLi, XSS, CSRF, etc.).', 2, NULL),
(27, 9, 'SEC-03', 'Gestión de Accesos e Identidades (IAM)', 'Políticas de mínimo privilegio y autenticación segura.', 3, NULL),
(28, 10, 'BDA-01', 'Diseño de Esquemas', 'Normalización y modelado relacional y no relacional.', 1, NULL),
(29, 10, 'BDA-02', 'Optimización de Consultas (Tuning)', 'Análisis de planes de ejecución e índices eficientes.', 2, NULL),
(30, 10, 'BDA-03', 'Integridad de Datos', 'Manejo de transacciones ACID, bloqueos y consistencia.', 3, NULL),
(31, 11, 'NEG-01', 'Identificación de Necesidades', 'Indagación consultiva y escucha activa del cliente.', 1, NULL),
(32, 11, 'NEG-02', 'Manejo de Objeciones', 'Superación constructiva de barreras del cliente.', 2, NULL),
(33, 11, 'NEG-03', 'Cierre de Acuerdos', 'Estrategias de negociación contractual ganar-ganar.', 3, NULL),
(34, 12, 'GES-01', 'Planificación y Cronograma', 'Estimación precisa de esfuerzo y plazos de entrega.', 1, NULL),
(35, 12, 'GES-02', 'Gestión de Riesgos', 'Identificación temprana y mitigación de cuellos de botella.', 2, NULL),
(36, 12, 'GES-03', 'Métricas Ágiles', 'Uso de velocidad, burndown, lead time y cycle time.', 3, NULL),
(37, 13, 'UXD-01', 'Investigación de Usuarios', 'Entrevistas, encuestas y pruebas de usabilidad.', 1, NULL),
(38, 13, 'UXD-02', 'Prototipado', 'Creación de wireframes interactivos y mockups de alta fidelidad.', 2, NULL),
(39, 13, 'UXD-03', 'Sistemas de Diseño (Design Systems)', 'Mantenimiento de bibliotecas de componentes reutilizables.', 3, NULL),
(40, 14, 'IAE-01', 'Entrenamiento y Fine-tuning', 'Ajuste de hiperparámetros y optimización de modelos.', 1, NULL),
(41, 14, 'IAE-02', 'Ingeniería de Prompts', 'Diseño y optimización de instrucciones para LLMs.', 2, NULL),
(42, 14, 'IAE-03', 'Evaluación de Sesgo y Rendimiento', 'Métricas de precisión, recall, F1 y equidad.', 3, NULL),
(43, 15, 'AUT-01', 'Estrategia de Pruebas', 'Diseño de la pirámide de testing (unitarias, integración, E2E).', 1, NULL),
(44, 15, 'AUT-02', 'Scripts Automatizados', 'Uso de frameworks como Cypress, Playwright, Selenium y JUnit.', 2, NULL),
(45, 15, 'AUT-03', 'Pruebas de Carga y Estrés', 'Evaluación de rendimiento con herramientas como k6 o JMeter.', 3, NULL),
(46, 16, 'RES-01', 'Pensamiento Crítico', 'Cuestionamiento constructivo de premisas y supuestos.', 1, NULL),
(47, 16, 'RES-02', 'Análisis de Causa Raíz', 'Metodología de los 5 Porqués y diagrama de Ishikawa.', 2, NULL),
(48, 16, 'RES-03', 'Pragmatismo', 'Equilibrio entre la solución ideal y los tiempos de negocio.', 3, NULL),
(49, 17, 'ORIENT-01', 'Comprensión del Usuario', 'Empatía con los problemas reales del cliente.', 1, NULL),
(50, 17, 'ORIENT-02', 'Cumplimiento de SLAs', 'Respeto de compromisos y tiempos de respuesta acordados.', 2, NULL),
(51, 17, 'ORIENT-03', 'Feedback Continuo', 'Incorporación sistemática de sugerencias de los usuarios.', 3, NULL),
(52, 18, 'INNOV-01', 'Generación de Ideas', 'Dinámicas de ideación y pensamiento lateral.', 1, NULL),
(53, 18, 'INNOV-02', 'Prototipado Rápido', 'Validación de hipótesis con productos mínimos viables (MVPs).', 2, NULL),
(54, 18, 'INNOV-03', 'Vigilancia Tecnológica', 'Investigación constante de tendencias de la industria.', 3, NULL);

-- ---------------------------------------------------------------------
-- 6. PREGUNTAS (220 Preguntas)
-- ---------------------------------------------------------------------
INSERT INTO preguntas (id, factor_id, version, nombre, texto, descripcion, tipo, fecha_modificacion, fecha_baja) VALUES
-- Competencia 1 (LID)
(1, 1, 1, 'LID-01-P1', '¿Cómo actuás frente a decisiones críticas cuando la información disponible es incompleta?', 'Decisiones rápidas y fundamentadas.', 'SINGLE_CHOICE', NOW(), NULL),
(2, 2, 1, 'LID-02-P1', '¿De qué manera mantenés motivado al equipo durante momentos de alta presión o entregas críticas?', 'Motivación directa del equipo.', 'MULTIPLE_CHOICE', NOW(), NULL),
(3, 2, 1, 'LID-02-P2', '¿Qué estrategia aplicás para sostener el compromiso a largo plazo en proyectos extensos?', 'Sostenibilidad del compromiso.', 'SINGLE_CHOICE', NOW(), NULL),
(4, 3, 1, 'LID-03-P1', '¿Qué criterios utilizás para delegar una tarea compleja a un miembro del equipo?', 'Criterios de delegación.', 'SINGLE_CHOICE', NOW(), NULL),
(5, 3, 1, 'LID-03-P2', '¿Cómo supervisás el avance de las tareas delegadas sin caer en micromanagement?', 'Supervisión y autonomía.', 'MULTIPLE_CHOICE', NOW(), NULL),
(6, 3, 1, 'LID-03-P3', '¿Qué medidas tomás si un colaborador no logra cumplir con la tarea delegada en tiempo y forma?', 'Gestión del error y aprendizaje.', 'SINGLE_CHOICE', NOW(), NULL),

-- Competencia 2 (TEQ)
(7, 4, 1, 'TEQ-01-P1', '¿Compartís tus conocimientos y hallazgos técnicos con el equipo sin necesidad de que te lo soliciten?', 'Proactividad en el equipo.', 'SINGLE_CHOICE', NOW(), NULL),
(8, 5, 1, 'TEQ-02-P1', '¿Cómo abordás un desacuerdo interpersonal técnico dentro del grupo de trabajo?', 'Mediación constructiva.', 'MULTIPLE_CHOICE', NOW(), NULL),
(9, 6, 1, 'TEQ-03-P1', '¿Escuchás activamente las observaciones y sugerencias de tus compañeros durante las revisiones?', 'Escucha activa.', 'SINGLE_CHOICE', NOW(), NULL),

-- Competencia 3 (PAN)
(10, 7, 1, 'PAN-01-P1', '¿Cómo analizás métricas de rendimiento para detectar anomalías o inconsistencias?', 'Análisis de datos.', 'SINGLE_CHOICE', NOW(), NULL),
(11, 7, 1, 'PAN-01-P2', '¿Qué herramientas utilizás para validar la consistencia de tus fuentes de datos?', 'Herramientas analíticas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(12, 8, 1, 'PAN-02-P1', '¿Cómo descomponés un problema complejo en sub-tareas manejables antes de comenzar a codificar?', 'Descomposición analítica.', 'SINGLE_CHOICE', NOW(), NULL),
(13, 8, 1, 'PAN-02-P2', '¿Identificás dependencias críticas y cuellos de botella antes de iniciar la implementación?', 'Análisis de dependencias.', 'MULTIPLE_CHOICE', NOW(), NULL),
(14, 8, 1, 'PAN-02-P3', '¿Qué criterios de priorización aplicás al abordar la primera sub-tarea de un requerimiento?', 'Criterios de priorización.', 'SINGLE_CHOICE', NOW(), NULL),
(15, 9, 1, 'PAN-03-P1', '¿Podés extraer tendencias válidas y conclusiones a partir de muestras reducidas de datos?', 'Inferencia estadística.', 'SINGLE_CHOICE', NOW(), NULL),
(16, 9, 1, 'PAN-03-P2', '¿Cómo validás hipótesis técnicas utilizando evidencia cuantitativa?', 'Validación con evidencia.', 'MULTIPLE_CHOICE', NOW(), NULL),

-- Competencia 4 (PRG)
(17, 10, 1, 'PRG-01-P1', '¿Aplicás principios SOLID y Clean Code en tus desarrollos cotidianos?', 'Buenas prácticas y diseño limpio.', 'SINGLE_CHOICE', NOW(), NULL),
(18, 10, 1, 'PRG-01-P2', '¿Con qué frecuencia realizás revisiones de código (Code Reviews) con estándares rigurosos?', 'Revisiones de código entre pares.', 'MULTIPLE_CHOICE', NOW(), NULL),
(19, 10, 1, 'PRG-01-P3', '¿Cómo asegurás que tu código sea legible y fácil de mantener por otros desarrolladores?', 'Mantenibilidad del código.', 'SINGLE_CHOICE', NOW(), NULL),
(20, 11, 1, 'PRG-02-P1', '¿Cuál es tu metodología para reproducir y aislar un bug intermitente en producción?', 'Depuración metódica.', 'SINGLE_CHOICE', NOW(), NULL),
(21, 11, 1, 'PRG-02-P2', '¿Utilizás logs estructurados y herramientas APM para diagnosticar cuellos de botella?', 'Observabilidad y diagnóstico.', 'MULTIPLE_CHOICE', NOW(), NULL),
(22, 11, 1, 'PRG-02-P3', '¿Cómo evaluás el impacto para priorizar la corrección de errores en sistemas productivos?', 'Evaluación de impacto.', 'SINGLE_CHOICE', NOW(), NULL),
(23, 11, 1, 'PRG-02-P4', '¿Escribís pruebas unitarias que fallen antes de corregir un error para evitar regresiones?', 'Pruebas de regresión.', 'SINGLE_CHOICE', NOW(), NULL),
(24, 12, 1, 'PRG-03-P1', '¿Analizás la complejidad temporal y espacial (Big-O) de tus algoritmos antes de implementarlos?', 'Complejidad algorítmica.', 'SINGLE_CHOICE', NOW(), NULL),
(25, 12, 1, 'PRG-03-P2', '¿Cómo optimizás el uso de memoria en procesos con grandes volúmenes de datos?', 'Gestión eficiente de memoria.', 'MULTIPLE_CHOICE', NOW(), NULL),
(26, 12, 1, 'PRG-03-P3', '¿Cuándo decidís refactorizar un algoritmo por motivos de rendimiento?', 'Refactorización y performance.', 'SINGLE_CHOICE', NOW(), NULL),
(27, 12, 1, 'PRG-03-P4', '¿Seleccionás estructuras de datos adecuadas (HashMap, TreeMap, List, Set) según el caso de uso?', 'Estructuras de datos óptimas.', 'SINGLE_CHOICE', NOW(), NULL),
(28, 12, 1, 'PRG-03-P5', '¿Medís el impacto de tus optimizaciones mediante benchmarks antes y después del cambio?', 'Benchmarking y métricas.', 'MULTIPLE_CHOICE', NOW(), NULL),

-- Competencia 5 (COM)
(29, 13, 1, 'COM-01-P1', '¿Cómo transmitís feedback constructivo de manera oral y respetuosa a un compañero?', 'Claridad y empatía oral.', 'SINGLE_CHOICE', NOW(), NULL),
(30, 14, 1, 'COM-02-P1', '¿Cómo estructurás la documentación técnica de una API o servicio?', 'Documentación de arquitectura.', 'SINGLE_CHOICE', NOW(), NULL),
(31, 14, 1, 'COM-02-P2', '¿Mantenés actualizados los archivos README y guías de contribución del repositorio?', 'Mantenimiento de documentación.', 'MULTIPLE_CHOICE', NOW(), NULL),
(32, 14, 1, 'COM-02-P3', '¿Qué herramientas utilizás para documentar endpoints y contratos (Swagger/OpenAPI)?', 'Especificación de contratos API.', 'SINGLE_CHOICE', NOW(), NULL),
(33, 15, 1, 'COM-03-P1', '¿Cómo expresás un desacuerdo técnico fundamentado con una decisión de arquitectura?', 'Asertividad técnica.', 'SINGLE_CHOICE', NOW(), NULL),
(34, 15, 1, 'COM-03-P2', '¿Sabés establecer límites claros cuando la carga de trabajo supera tu capacidad?', 'Gestión de capacidad.', 'MULTIPLE_CHOICE', NOW(), NULL),
(35, 15, 1, 'COM-03-P3', '¿Cómo gestionás situaciones de comunicación tensas o agresivas con clientes o partes interesadas?', 'Resolución de tensión.', 'SINGLE_CHOICE', NOW(), NULL),
(36, 15, 1, 'COM-03-P4', '¿Defendés los intereses técnicos y el bienestar de tu equipo en negociaciones de plazos?', 'Defensa del equipo.', 'MULTIPLE_CHOICE', NOW(), NULL);

-- Inserción distribuida de preguntas 37 a 220
INSERT INTO preguntas (id, factor_id, version, nombre, texto, descripcion, tipo, fecha_modificacion, fecha_baja) VALUES
(37, 16, 1, 'PREG-037', '¿Cómo reaccionás frente a un cambio repentino en los requerimientos del sprint?', 'Adaptabilidad ante cambios.', 'SINGLE_CHOICE', NOW(), NULL),
(38, 16, 1, 'PREG-038', '¿Te adaptás con facilidad a nuevas metodologías o estructuras de equipo?', 'Flexibilidad organizativa.', 'MULTIPLE_CHOICE', NOW(), NULL),
(39, 16, 1, 'PREG-039', '¿Cómo gestionás la cancelación imprevista de un proyecto en el que invertiste esfuerzo?', 'Resiliencia profesional.', 'SINGLE_CHOICE', NOW(), NULL),
(40, 17, 1, 'PREG-040', '¿Dedicás tiempo regularmente a capacitarte en nuevas tecnologías?', 'Autoaprendizaje técnico.', 'SINGLE_CHOICE', NOW(), NULL),
(41, 17, 1, 'PREG-041', '¿Compartís tutoriales, artículos o cursos con tus compañeros de equipo?', 'Cultura de aprendizaje.', 'MULTIPLE_CHOICE', NOW(), NULL),
(42, 17, 1, 'PREG-042', '¿Cómo abordás una tecnología o framework que nunca utilizaste antes?', 'Curva de aprendizaje.', 'SINGLE_CHOICE', NOW(), NULL),
(43, 17, 1, 'PREG-043', '¿Participás en webinars, conferencias o comunidades de desarrollo de software?', 'Actualización continua.', 'MULTIPLE_CHOICE', NOW(), NULL),
(44, 18, 1, 'PREG-044', '¿Cómo te reponés ante un fallo grave en un despliegue a producción?', 'Tolerancia al error.', 'SINGLE_CHOICE', NOW(), NULL),
(45, 18, 1, 'PREG-045', '¿Mantenés la compostura y el pensamiento claro bajo fechas de entrega muy ajustadas?', 'Gestión del estrés.', 'MULTIPLE_CHOICE', NOW(), NULL),
(46, 18, 1, 'PREG-046', '¿Cómo recibís y procesás devoluciones o evaluaciones de desempeño desfavorables?', 'Aceptación de feedback.', 'SINGLE_CHOICE', NOW(), NULL),
(47, 18, 1, 'PREG-047', '¿Brindás apoyo a compañeros que están pasando por momentos de frustración laboral?', 'Empatía y contención.', 'MULTIPLE_CHOICE', NOW(), NULL),
(48, 19, 1, 'PREG-048', '¿Cuáles son tus criterios para descomponer un monolito en microservicios independientes?', 'Descomposición por dominios.', 'SINGLE_CHOICE', NOW(), NULL),
(49, 19, 1, 'PREG-049', '¿Diseñás endpoints idempotentes para prevenir duplicación en reintentos?', 'Idempotencia en APIs.', 'MULTIPLE_CHOICE', NOW(), NULL),
(50, 19, 1, 'PREG-050', '¿Cómo gestionás transacciones distribuidas y consistencia eventual (Patrón Saga)?', 'Transacciones distribuidas.', 'SINGLE_CHOICE', NOW(), NULL),
(51, 19, 1, 'PREG-051', '¿Utilizás arquitecturas orientadas a eventos con brokers como Kafka o RabbitMQ?', 'Event-driven architecture.', 'MULTIPLE_CHOICE', NOW(), NULL),
(52, 20, 1, 'PREG-052', '¿Qué patrones de diseño GoF aplicás con mayor frecuencia en tus proyectos?', 'Patrones de diseño.', 'SINGLE_CHOICE', NOW(), NULL),
(53, 20, 1, 'PREG-053', '¿Aplicás Clean Architecture o Arquitectura Hexagonal para desacoplar la lógica de negocio?', 'Arquitectura limpia.', 'MULTIPLE_CHOICE', NOW(), NULL),
(54, 20, 1, 'PREG-054', '¿Cómo prevenís antipatrones como God Object o Feature Envy en tus clases?', 'Prevención de antipatrones.', 'SINGLE_CHOICE', NOW(), NULL),
(55, 20, 1, 'PREG-055', '¿Utilizás Inyección de Dependencias e Inversión de Control de forma consistente?', 'Inversión de dependencias.', 'MULTIPLE_CHOICE', NOW(), NULL),
(56, 21, 1, 'PREG-056', '¿Cómo diseñás una aplicación para permitir escalabilidad horizontal sin estado (Stateless)?', 'Escalabilidad horizontal.', 'SINGLE_CHOICE', NOW(), NULL),
(57, 21, 1, 'PREG-057', '¿Implementás capas de caché distribuida (Redis / Memcached) para reducir carga en base de datos?', 'Estrategias de caché.', 'MULTIPLE_CHOICE', NOW(), NULL),
(58, 21, 1, 'PREG-058', '¿Cómo dimensionás y gestionás el pool de conexiones a la base de datos (HikariCP)?', 'Pool de conexiones.', 'SINGLE_CHOICE', NOW(), NULL),
(59, 21, 1, 'PREG-059', '¿Ejecutás pruebas de estrés previo a lanzamientos con alta demanda esperada?', 'Pruebas de capacidad.', 'MULTIPLE_CHOICE', NOW(), NULL),
(60, 22, 1, 'PREG-060', '¿Cómo estructurás un pipeline de GitHub Actions o GitLab CI con etapas de lint, test y deploy?', 'Estructura de pipelines.', 'SINGLE_CHOICE', NOW(), NULL),
(61, 22, 1, 'PREG-061', '¿Implementás mecanismos automáticos de rollback en caso de fallos en el despliegue?', 'Estrategia de rollback.', 'MULTIPLE_CHOICE', NOW(), NULL),
(62, 22, 1, 'PREG-062', '¿Cómo protegés secretos y credenciales en tus entornos de integración continua?', 'Seguridad de credenciales.', 'SINGLE_CHOICE', NOW(), NULL),
(63, 22, 1, 'PREG-063', '¿Utilizás estrategias de despliegue Blue/Green o Canary para minimizar el tiempo de inactividad?', 'Estrategias de despliegue.', 'MULTIPLE_CHOICE', NOW(), NULL),
(64, 23, 1, 'PREG-064', '¿Cómo gestionás el estado (state) de tu infraestructura con Terraform?', 'Gestión de infraestructura.', 'SINGLE_CHOICE', NOW(), NULL),
(65, 23, 1, 'PREG-065', '¿Modularizás tu código de infraestructura como código (IaC) para facilitar su reutilización?', 'Modularidad en IaC.', 'MULTIPLE_CHOICE', NOW(), NULL),
(66, 23, 1, 'PREG-066', '¿Validás y probás los planes de ejecución de Terraform antes de aplicarlos en producción?', 'Validación previa de IaC.', 'SINGLE_CHOICE', NOW(), NULL),
(67, 23, 1, 'PREG-067', '¿Aplicás principios de infraestructura inmutable reemplazando instancias en lugar de modificarlas?', 'Infraestructura inmutable.', 'MULTIPLE_CHOICE', NOW(), NULL),
(68, 24, 1, 'PREG-068', '¿Cómo configurás tableros en Grafana para visualizar métricas críticas de tus servicios?', 'Visualización de métricas.', 'SINGLE_CHOICE', NOW(), NULL),
(69, 24, 1, 'PREG-069', '¿Configurás alertas inteligentes con enrutamiento adecuado evitando la fatiga de alertas?', 'Gestión de alertas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(70, 24, 1, 'PREG-070', '¿Cómo definís objetivos de nivel de servicio (SLOs) e indicadores clave (SLIs)?', 'Definición de SLOs.', 'SINGLE_CHOICE', NOW(), NULL),
(71, 24, 1, 'PREG-071', '¿Monitoreás presupuestos de error (Error Budgets) para equilibrar innovación y estabilidad?', 'Presupuestos de error.', 'MULTIPLE_CHOICE', NOW(), NULL),
(72, 25, 1, 'PREG-072', '¿Cómo ejecutás análisis estático de vulnerabilidades (SAST) con herramientas como SonarQube o Snyk?', 'Análisis SAST.', 'SINGLE_CHOICE', NOW(), NULL),
(73, 25, 1, 'PREG-073', '¿Auditás regularmente dependencias de terceros para mitigar vulnerabilidades conocidas (CVEs)?', 'Auditoría de dependencias.', 'MULTIPLE_CHOICE', NOW(), NULL),
(74, 25, 1, 'PREG-074', '¿Cómo priorizás la remediación de vulnerabilidades según su puntuación CVSS?', 'Priorización por severidad.', 'SINGLE_CHOICE', NOW(), NULL),
(75, 25, 1, 'PREG-075', '¿Realizás pruebas dinámicas de seguridad (DAST) contra tus endpoints en ambientes de prueba?', 'Pruebas DAST.', 'MULTIPLE_CHOICE', NOW(), NULL),
(76, 26, 1, 'PREG-076', '¿Cómo prevenís ataques de inyección SQL utilizando consultas parametrizadas u ORMs?', 'Mitigación de SQL Injection.', 'SINGLE_CHOICE', NOW(), NULL),
(77, 26, 1, 'PREG-077', '¿Seguís las directrices del OWASP Top 10 durante la fase de diseño y desarrollo?', 'Estándares OWASP.', 'MULTIPLE_CHOICE', NOW(), NULL),
(78, 26, 1, 'PREG-078', '¿Cómo asegurás el cifrado adecuado de datos sensibles en reposo y en tránsito (TLS/HTTPS)?', 'Cifrado de datos.', 'SINGLE_CHOICE', NOW(), NULL),
(79, 26, 1, 'PREG-079', '¿Sanitizás y validás todas las entradas del usuario en el backend sin confiar en el frontend?', 'Validación defensiva.', 'MULTIPLE_CHOICE', NOW(), NULL),
(80, 27, 1, 'PREG-080', '¿Cómo aplicás el principio de mínimo privilegio en roles y políticas de seguridad IAM?', 'Mínimo privilegio.', 'SINGLE_CHOICE', NOW(), NULL),
(81, 27, 1, 'PREG-081', '¿Rotás periódicamente claves de API, tokens de servicio y certificados SSL?', 'Rotación de credenciales.', 'MULTIPLE_CHOICE', NOW(), NULL),
(82, 27, 1, 'PREG-082', '¿Cómo implementás autenticación y autorización segura con OAuth2 y JWT?', 'Autenticación con JWT.', 'SINGLE_CHOICE', NOW(), NULL),
(83, 27, 1, 'PREG-083', '¿Exigís autenticación multifactor (MFA) para accesos a sistemas críticos y servidores?', 'Autenticación multifactor.', 'MULTIPLE_CHOICE', NOW(), NULL),
(84, 28, 1, 'PREG-084', '¿Cómo aplicás normalización relacional hasta tercera forma normal (3NF) de manera óptima?', 'Normalización de bases de datos.', 'SINGLE_CHOICE', NOW(), NULL),
(85, 28, 1, 'PREG-085', '¿Bajo qué criterios decidís utilizar una base de datos relacional (SQL) frente a una NoSQL?', 'Selección de motor de base de datos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(86, 28, 1, 'PREG-086', '¿Cómo estructurás migraciones de base de datos automatizadas con Flyway o Liquibase?', 'Migraciones automatizadas.', 'SINGLE_CHOICE', NOW(), NULL),
(87, 28, 1, 'PREG-087', '¿Utilizás claves foráneas, restricciones de unicidad e índices para garantizar consistencia?', 'Integridad referencial.', 'MULTIPLE_CHOICE', NOW(), NULL),
(88, 29, 1, 'PREG-088', '¿Cómo analizás el plan de ejecución de una consulta lenta mediante EXPLAIN ANALYZE?', 'Análisis de planes de ejecución.', 'SINGLE_CHOICE', NOW(), NULL),
(89, 29, 1, 'PREG-089', '¿Creás índices B-Tree, GiST o GIN según los patrones de búsqueda más frecuentes?', 'Estrategias de indexación.', 'MULTIPLE_CHOICE', NOW(), NULL),
(90, 29, 1, 'PREG-090', '¿Cómo monitoreás y resolvés consultas lentas registradas en los logs de producción?', 'Optimización de consultas.', 'SINGLE_CHOICE', NOW(), NULL),
(91, 29, 1, 'PREG-091', '¿Cómo evitás el problema de N+1 consultas al trabajar con frameworks ORM como Hibernate/JPA?', 'Prevención de N+1 queries.', 'MULTIPLE_CHOICE', NOW(), NULL),
(92, 30, 1, 'PREG-092', '¿Cómo seleccionás el nivel de aislamiento de transacciones adecuado (Read Committed, Serializable)?', 'Niveles de aislamiento.', 'SINGLE_CHOICE', NOW(), NULL),
(93, 30, 1, 'PREG-093', '¿Cómo prevenís y gestionás situaciones de interbloqueo (Deadlocks) en la base de datos?', 'Gestión de deadlocks.', 'MULTIPLE_CHOICE', NOW(), NULL),
(94, 30, 1, 'PREG-094', '¿Cómo planificás estrategias de backup periódico y recuperación punto en el tiempo (PITR)?', 'Recuperación ante desastres.', 'SINGLE_CHOICE', NOW(), NULL),
(95, 30, 1, 'PREG-095', '¿Auditás cambios en tablas sensibles utilizando triggers o tablas de auditoría histórica?', 'Auditoría de datos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(96, 31, 1, 'PREG-096', '¿Cómo descubrís los puntos de dolor y necesidades reales de un cliente en una reunión inicial?', 'Indagación de necesidades.', 'SINGLE_CHOICE', NOW(), NULL),
(97, 31, 1, 'PREG-097', '¿Practicás escucha activa y repreguntás para confirmar requerimientos comerciales?', 'Escucha activa comercial.', 'MULTIPLE_CHOICE', NOW(), NULL),
(98, 31, 1, 'PREG-098', '¿Cómo calificás oportunidades comerciales para determinar su viabilidad técnica y económica?', 'Calificación de oportunidades.', 'SINGLE_CHOICE', NOW(), NULL),
(99, 31, 1, 'PREG-099', '¿Alineás las propuestas de solución técnica con el retorno de inversión (ROI) del cliente?', 'Propuesta de valor.', 'MULTIPLE_CHOICE', NOW(), NULL),
(100, 32, 1, 'PREG-100', '¿Cómo convertís objeciones de precio en conversaciones sobre valor y calidad técnica?', 'Manejo de objeciones.', 'SINGLE_CHOICE', NOW(), NULL),
(101, 32, 1, 'PREG-101', '¿Anticipás objeciones técnicas de los arquitectos del cliente preparando casos de prueba?', 'Anticipación técnica.', 'MULTIPLE_CHOICE', NOW(), NULL),
(102, 32, 1, 'PREG-102', '¿Cómo gestionás requerimientos de funcionalidades que el producto no posee actualmente?', 'Gestión de brechas funcionales.', 'SINGLE_CHOICE', NOW(), NULL),
(103, 32, 1, 'PREG-103', '¿Colaborás estrechamente con ingenieros de preventa para resolver dudas técnicas del cliente?', 'Trabajo con preventa.', 'MULTIPLE_CHOICE', NOW(), NULL),
(104, 33, 1, 'PREG-104', '¿Qué técnicas utilizás para acelerar el cierre de acuerdos ante plazos comerciales límite?', 'Técnicas de cierre.', 'SINGLE_CHOICE', NOW(), NULL),
(105, 33, 1, 'PREG-105', '¿Negociás acuerdos de nivel de servicio (SLA) realistas con los equipos legales del cliente?', 'Negociación de contratos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(106, 33, 1, 'PREG-106', '¿Cómo coordinás procesos de aprobación que involucran a múltiples tomadores de decisión?', 'Gestión de interesados.', 'SINGLE_CHOICE', NOW(), NULL),
(107, 33, 1, 'PREG-107', '¿Asegurás compromisos de renovación y crecimiento a largo plazo con clientes corporativos?', 'Fidelización comercial.', 'MULTIPLE_CHOICE', NOW(), NULL),
(108, 34, 1, 'PREG-108', '¿Cómo estimás puntos de historia utilizando Planning Poker u otras técnicas ágiles?', 'Estimación ágil.', 'SINGLE_CHOICE', NOW(), NULL),
(109, 34, 1, 'PREG-109', '¿Considerás la deuda técnica y tareas de refactorización en las estimaciones del sprint?', 'Inclusión de deuda técnica.', 'MULTIPLE_CHOICE', NOW(), NULL),
(110, 34, 1, 'PREG-110', '¿Cómo gestionás el camino crítico y dependencias entre equipos en proyectos complejos?', 'Camino crítico.', 'SINGLE_CHOICE', NOW(), NULL),
(111, 34, 1, 'PREG-111', '¿Ajustás el alcance del proyecto de forma negociada cuando los plazos son inamovibles?', 'Ajuste de alcance.', 'MULTIPLE_CHOICE', NOW(), NULL),
(112, 35, 1, 'PREG-112', '¿Cómo construís una matriz de riesgos para identificar amenazas tempranas en el proyecto?', 'Matriz de riesgos.', 'SINGLE_CHOICE', NOW(), NULL),
(113, 35, 1, 'PREG-113', '¿Establecés planes de mitigación y contingencia para los riesgos de mayor probabilidad e impacto?', 'Planes de mitigación.', 'MULTIPLE_CHOICE', NOW(), NULL),
(114, 35, 1, 'PREG-114', '¿Monitoreás fluctuaciones en la velocidad del equipo para prevenir desviaciones del cronograma?', 'Seguimiento de velocidad.', 'SINGLE_CHOICE', NOW(), NULL),
(115, 35, 1, 'PREG-115', '¿Escalás bloqueos a los líderes y sponsors del proyecto de manera oportuna?', 'Escalamiento de bloqueos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(116, 36, 1, 'PREG-116', '¿Cómo interpretás los gráficos de avance (Burndown Charts) durante el sprint?', 'Interpretación de métricas.', 'SINGLE_CHOICE', NOW(), NULL),
(117, 36, 1, 'PREG-117', '¿Hacés seguimiento de métricas de Lead Time y Cycle Time para optimizar el flujo de trabajo?', 'Métricas de flujo.', 'MULTIPLE_CHOICE', NOW(), NULL),
(118, 36, 1, 'PREG-118', '¿Utilizás los compromisos de las retrospectivas para implementar mejoras continuas concretas?', 'Mejora en retrospectivas.', 'SINGLE_CHOICE', NOW(), NULL),
(119, 36, 1, 'PREG-119', '¿Evitás el crecimiento descontrolado del alcance (Scope Creep) durante la ejecución del sprint?', 'Control de alcance.', 'MULTIPLE_CHOICE', NOW(), NULL),
(120, 37, 1, 'PREG-120', '¿Cómo planificás y conducís entrevistas cualitativas con usuarios finales?', 'Entrevistas con usuarios.', 'SINGLE_CHOICE', NOW(), NULL),
(121, 37, 1, 'PREG-121', '¿Construís arquetipos de usuario (User Personas) fundamentados en datos reales de investigación?', 'User Personas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(122, 37, 1, 'PREG-122', '¿Cómo analizás las grabaciones y notas de pruebas de usabilidad para extraer aprendizajes?', 'Análisis de usabilidad.', 'SINGLE_CHOICE', NOW(), NULL),
(123, 37, 1, 'PREG-123', '¿Mapeás exhaustivamente los mapas de experiencia del cliente (Customer Journey Maps)?', 'Journey Maps.', 'MULTIPLE_CHOICE', NOW(), NULL),
(124, 38, 1, 'PREG-124', '¿Cómo diseñás prototipos interactivos navegables en herramientas como Figma?', 'Prototipado interactivo.', 'SINGLE_CHOICE', NOW(), NULL),
(125, 38, 1, 'PREG-125', '¿Validás wireframes de baja fidelidad con usuarios antes de pasar a la etapa de diseño visual?', 'Validación temprana.', 'MULTIPLE_CHOICE', NOW(), NULL),
(126, 38, 1, 'PREG-126', '¿Diseñás pensando en interfaces adaptables (Mobile-First y Responsive Design)?', 'Diseño responsive.', 'SINGLE_CHOICE', NOW(), NULL),
(127, 38, 1, 'PREG-127', '¿Verificás contrastes de color y accesibilidad según pautas WCAG en todas tus pantallas?', 'Accesibilidad WCAG.', 'MULTIPLE_CHOICE', NOW(), NULL),
(128, 39, 1, 'PREG-128', '¿Cómo organizás bibliotecas de componentes reutilizables en Figma y Storybook?', 'Bibliotecas de componentes.', 'SINGLE_CHOICE', NOW(), NULL),
(129, 39, 1, 'PREG-129', '¿Mantenés consistencia en tokens de diseño (colores, tipografía, espaciados) entre plataformas?', 'Tokens de diseño.', 'MULTIPLE_CHOICE', NOW(), NULL),
(130, 39, 1, 'PREG-130', '¿Cómo coordinás el traspaso de diseño (Design Handoff) hacia los desarrolladores frontend?', 'Traspaso a desarrollo.', 'SINGLE_CHOICE', NOW(), NULL),
(131, 39, 1, 'PREG-131', '¿Auditás inconsistencias visuales en la aplicación en producción para mejorar el sistema de diseño?', 'Auditoría visual.', 'MULTIPLE_CHOICE', NOW(), NULL),
(132, 40, 1, 'PREG-132', '¿Cómo dividís conjuntos de datos en entrenamiento, validación y prueba para evitar fugas (data leakage)?', 'Partición de datasets.', 'SINGLE_CHOICE', NOW(), NULL),
(133, 40, 1, 'PREG-133', '¿Qué técnicas de regularización (L1/L2, Dropout) utilizás para mitigar el sobreajuste (Overfitting)?', 'Prevención de overfitting.', 'MULTIPLE_CHOICE', NOW(), NULL),
(134, 40, 1, 'PREG-134', '¿Cómo optimizás hiperparámetros utilizando técnicas como Random Search, Grid Search u Optuna?', 'Ajuste de hiperparámetros.', 'SINGLE_CHOICE', NOW(), NULL),
(135, 40, 1, 'PREG-135', '¿Hacés seguimiento de experimentos de modelos con herramientas como MLflow o Weights & Biases?', 'Seguimiento de experimentos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(136, 41, 1, 'PREG-136', '¿Cómo estructurás prompts avanzados con técnicas como Few-Shot y Chain-of-Thought para LLMs?', 'Ingeniería de prompts.', 'SINGLE_CHOICE', NOW(), NULL),
(137, 41, 1, 'PREG-137', '¿Implementás arquitecturas de Generación Aumentada por Recuperación (RAG) con bases vectoriales?', 'Arquitectura RAG.', 'MULTIPLE_CHOICE', NOW(), NULL),
(138, 41, 1, 'PREG-138', '¿Qué mecanismos utilizás para mitigar y detectar alucinaciones en las respuestas generadas por IA?', 'Mitigación de alucinaciones.', 'SINGLE_CHOICE', NOW(), NULL),
(139, 41, 1, 'PREG-139', '¿Evaluás modelos de embeddings vectoriales (pgvector / Pinecone) para búsqueda semántica precisa?', 'Embeddings y búsqueda semántica.', 'MULTIPLE_CHOICE', NOW(), NULL),
(140, 42, 1, 'PREG-140', '¿Cómo analizás métricas de Precision, Recall y F1-Score para evaluar clasificadores?', 'Métricas de evaluación de IA.', 'SINGLE_CHOICE', NOW(), NULL),
(141, 42, 1, 'PREG-141', '¿Auditás datasets de entrenamiento para detectar y mitigar sesgos demográficos o de género?', 'Auditoría de sesgos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(142, 42, 1, 'PREG-142', '¿Utilizás técnicas de explicabilidad como SHAP o LIME para interpretar decisiones del modelo?', 'Explicabilidad de IA.', 'SINGLE_CHOICE', NOW(), NULL),
(143, 42, 1, 'PREG-143', '¿Monitoreás el decaimiento del modelo (Data Drift / Concept Drift) en producción?', 'Monitoreo de drift.', 'MULTIPLE_CHOICE', NOW(), NULL),
(144, 43, 1, 'PREG-144', '¿Cómo diseñás la pirámide de pruebas equilibrando tests unitarios, de integración y E2E?', 'Pirámide de testing.', 'SINGLE_CHOICE', NOW(), NULL),
(145, 43, 1, 'PREG-145', '¿Definís criterios de aceptación claros y verificables en las historias de usuario?', 'Criterios de aceptación.', 'MULTIPLE_CHOICE', NOW(), NULL),
(146, 43, 1, 'PREG-146', '¿Bajo qué criterios decidís automatizar pruebas a nivel de API versus pruebas de interfaz de usuario?', 'Estrategia de automatización.', 'SINGLE_CHOICE', NOW(), NULL),
(147, 43, 1, 'PREG-147', '¿Integrás la ejecución de suites de pruebas automáticas en cada commit en los pipelines CI/CD?', 'Integración de pruebas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(148, 44, 1, 'PREG-148', '¿Cómo implementás el patrón Page Object Model (POM) en frameworks como Cypress o Playwright?', 'Patrón Page Object Model.', 'SINGLE_CHOICE', NOW(), NULL),
(149, 44, 1, 'PREG-149', '¿Escribís pruebas automatizadas de servicios REST utilizando librerías como REST-Assured o Supertest?', 'Testing de APIs.', 'MULTIPLE_CHOICE', NOW(), NULL),
(150, 44, 1, 'PREG-150', '¿Cómo aislás y resolvés pruebas inestables (Flaky Tests) en tus suites automáticas?', 'Resolución de tests inestables.', 'SINGLE_CHOICE', NOW(), NULL),
(151, 44, 1, 'PREG-151', '¿Generás reportes visuales de ejecución de pruebas con herramientas como Allure Report?', 'Reportes de ejecución QA.', 'MULTIPLE_CHOICE', NOW(), NULL),
(152, 45, 1, 'PREG-152', '¿Cómo configurás escenarios de pruebas de carga concurrente utilizando herramientas como k6 o JMeter?', 'Pruebas de carga.', 'SINGLE_CHOICE', NOW(), NULL),
(153, 45, 1, 'PREG-153', '¿Analizás métricas de latencia en percentiles clave (p95, p99) ante picos de tráfico?', 'Métricas de latencia.', 'MULTIPLE_CHOICE', NOW(), NULL),
(154, 45, 1, 'PREG-154', '¿Cómo identificás componentes o consultas que generan cuellos de botella durante pruebas de estrés?', 'Detección de cuellos de botella.', 'SINGLE_CHOICE', NOW(), NULL),
(155, 45, 1, 'PREG-155', '¿Ejecutás pruebas de resistencia (Soak Testing) para detectar fugas de memoria a lo largo del tiempo?', 'Pruebas de resistencia.', 'MULTIPLE_CHOICE', NOW(), NULL),
(156, 46, 1, 'PREG-156', '¿Cómo cuestionás constructivamente supuestos de negocio que carecen de validación?', 'Pensamiento crítico aplicado.', 'SINGLE_CHOICE', NOW(), NULL),
(157, 46, 1, 'PREG-157', '¿Evaluás múltiples alternativas de solución técnica antes de decidirte por una de ellas?', 'Evaluación de alternativas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(158, 46, 1, 'PREG-158', '¿Identificás falacias lógicas o sesgos cognitivos en debates técnicos del equipo?', 'Identificación de falacias.', 'SINGLE_CHOICE', NOW(), NULL),
(159, 46, 1, 'PREG-159', '¿Priorizás el uso de datos y evidencias por sobre intuiciones al resolver disputas técnicas?', 'Decisiones basadas en datos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(160, 47, 1, 'PREG-160', '¿Cómo aplicás la técnica de los 5 Porqués en reuniones de post-mortem tras un incidente?', 'Técnica de los 5 Porqués.', 'SINGLE_CHOICE', NOW(), NULL),
(161, 47, 1, 'PREG-161', '¿Construís diagramas de causa-efecto (Ishikawa) para diagnosticar fallas sistémicas complejas?', 'Diagrama de Ishikawa.', 'MULTIPLE_CHOICE', NOW(), NULL),
(162, 47, 1, 'PREG-162', '¿Cómo prevenís la reaparición de un problema tras aplicar una solución temporal rápida (hotfix)?', 'Prevención de recurrencia.', 'SINGLE_CHOICE', NOW(), NULL),
(163, 47, 1, 'PREG-163', '¿Elaborás y publicás reportes post-mortem sin señalamiento de culpables (Blameless Post-Mortem)?', 'Cultura sin culpas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(164, 48, 1, 'PREG-164', '¿Cómo equilibrás la búsqueda de perfección técnica con la necesidad de entregar valor a tiempo?', 'Equilibrio y pragmatismo.', 'SINGLE_CHOICE', NOW(), NULL),
(165, 48, 1, 'PREG-165', '¿Aceptás contraer deuda técnica controlada para cumplir hitos comerciales estratégicos?', 'Deuda técnica controlada.', 'MULTIPLE_CHOICE', NOW(), NULL),
(166, 48, 1, 'PREG-166', '¿Elegís soluciones simples y directas en lugar de caer en sobreingeniería (Over-Engineering)?', 'Simplicidad de diseño.', 'SINGLE_CHOICE', NOW(), NULL),
(167, 48, 1, 'PREG-167', '¿Te focalizás en el valor que aporta la funcionalidad al usuario por sobre modas tecnológicas?', 'Orientación al valor.', 'MULTIPLE_CHOICE', NOW(), NULL),
(168, 49, 1, 'PREG-168', '¿Cómo recopilás feedback cualitativo directo de los usuarios que interactúan con el producto?', 'Recopilación de feedback.', 'SINGLE_CHOICE', NOW(), NULL),
(169, 49, 1, 'PREG-169', '¿Analizás patrones en tickets de soporte para identificar fricciones en la experiencia de usuario?', 'Análisis de soporte.', 'MULTIPLE_CHOICE', NOW(), NULL),
(170, 49, 1, 'PREG-170', '¿Defendés las necesidades del usuario final frente a requerimientos apresurados del negocio?', 'Defensa del usuario.', 'SINGLE_CHOICE', NOW(), NULL),
(171, 49, 1, 'PREG-171', '¿Hacés seguimiento de métricas de satisfacción del cliente como NPS o CSAT?', 'Métricas de satisfacción.', 'MULTIPLE_CHOICE', NOW(), NULL),
(172, 50, 1, 'PREG-172', '¿Cómo asegurás que los compromisos de disponibilidad del 99.9% se respeten en producción?', 'Cumplimiento de acuerdos.', 'SINGLE_CHOICE', NOW(), NULL),
(173, 50, 1, 'PREG-173', '¿Te comunicás con transparencia y agilidad ante caídas o degradaciones del servicio?', 'Comunicación ante incidentes.', 'MULTIPLE_CHOICE', NOW(), NULL),
(174, 50, 1, 'PREG-174', '¿Cómo priorizás la resolución de incidentes según la severidad y el impacto en el cliente?', 'Priorización por severidad.', 'SINGLE_CHOICE', NOW(), NULL),
(175, 50, 1, 'PREG-175', '¿Revisás periódicamente incidentes que afectaron el SLA con el equipo de operaciones?', 'Revisión de SLAs.', 'MULTIPLE_CHOICE', NOW(), NULL),
(176, 51, 1, 'PREG-176', '¿Cómo organizás y evaluás experimentos con pruebas A/B para validar nuevas funcionalidades?', 'Pruebas A/B.', 'SINGLE_CHOICE', NOW(), NULL),
(177, 51, 1, 'PREG-177', '¿Incorporás el feedback directo de los clientes en las prioridades del backlog del sprint?', 'Feedback en backlog.', 'MULTIPLE_CHOICE', NOW(), NULL),
(178, 51, 1, 'PREG-178', '¿Medís la tasa de adopción de nuevas funcionalidades después de cada lanzamiento?', 'Tasa de adopción.', 'SINGLE_CHOICE', NOW(), NULL),
(179, 51, 1, 'PREG-179', '¿Retirás o simplificás funcionalidades en desuso basándote en métricas reales de utilización?', 'Depuración de funciones.', 'MULTIPLE_CHOICE', NOW(), NULL),
(180, 52, 1, 'PREG-180', '¿Cómo facilitás sesiones de lluvia de ideas y dinámicas de ideación creativa en tu equipo?', 'Facilitación de ideas.', 'SINGLE_CHOICE', NOW(), NULL),
(181, 52, 1, 'PREG-181', '¿Fomentás la propuesta de ideas innovadoras antes de descartarlas por limitaciones técnicas?', 'Fomento a la innovación.', 'MULTIPLE_CHOICE', NOW(), NULL),
(182, 52, 1, 'PREG-182', '¿Conectás conceptos de diferentes dominios para diseñar características novedosas?', 'Pensamiento lateral.', 'SINGLE_CHOICE', NOW(), NULL),
(183, 52, 1, 'PREG-183', '¿Participás o impulsás hackathons internos para experimentar con soluciones innovadoras?', 'Hackathons y prototipos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(184, 53, 1, 'PREG-184', '¿Con qué rapidez podés construir un prototipo de prueba de concepto (PoC) para validar una idea?', 'Prueba de concepto rápida.', 'SINGLE_CHOICE', NOW(), NULL),
(185, 53, 1, 'PREG-185', '¿Utilizás herramientas low-code o scripts rápidos para validar hipótesis tempranas?', 'Herramientas de prototipado.', 'MULTIPLE_CHOICE', NOW(), NULL),
(186, 53, 1, 'PREG-186', '¿Cómo definís las métricas de éxito de un producto mínimo viable (MVP)?', 'Métricas de MVP.', 'SINGLE_CHOICE', NOW(), NULL),
(187, 53, 1, 'PREG-187', '¿Descartás prototipos que no cumplieron las expectativas sin apego emocional?', 'Desapego a prototipos fallidos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(188, 54, 1, 'PREG-188', '¿Cómo monitoreás la aparición de nuevos frameworks y herramientas en el ecosistema open-source?', 'Vigilancia tecnológica.', 'SINGLE_CHOICE', NOW(), NULL),
(189, 54, 1, 'PREG-189', '¿Consultás radares tecnológicos de la industria (ej: ThoughtWorks Tech Radar)?', 'Radares de tecnología.', 'MULTIPLE_CHOICE', NOW(), NULL),
(190, 54, 1, 'PREG-190', '¿Cómo evaluás la madurez y soporte comunitario antes de incorporar una librería externa?', 'Evaluación de dependencias.', 'SINGLE_CHOICE', NOW(), NULL),
(191, 54, 1, 'PREG-191', '¿Proponés actualizaciones tecnológicas fundamentadas con benchmarks comparativos?', 'Propuestas tecnológicas.', 'MULTIPLE_CHOICE', NOW(), NULL),
(192, 54, 1, 'PREG-192', '¿Compartís informes y resúmenes de novedades tecnológicas con toda la organización?', 'Difusión tecnológica.', 'SINGLE_CHOICE', NOW(), NULL),
(193, 17, 1, 'PREG-193', '¿Documentás los resultados de investigaciones y picos técnicos (Spikes) para consulta futura?', 'Documentación de spikes.', 'MULTIPLE_CHOICE', NOW(), NULL),
(194, 18, 1, 'PREG-194', '¿Cómo gestionás la presión personal durante incidentes de alta visibilidad en producción?', 'Autocontrol bajo presión.', 'SINGLE_CHOICE', NOW(), NULL),
(195, 19, 1, 'PREG-195', '¿Cómo definís la estrategia de versionado y deprecación de APIs para no afectar a clientes?', 'Deprecación de versiones.', 'MULTIPLE_CHOICE', NOW(), NULL),
(196, 20, 1, 'PREG-196', '¿Priorizás la composición sobre la herencia al diseñar jerarquías de clases?', 'Composición sobre herencia.', 'SINGLE_CHOICE', NOW(), NULL),
(197, 21, 1, 'PREG-197', '¿Cómo configurás réplicas de lectura (Read Replicas) para distribuir la carga en base de datos?', 'Réplicas de lectura.', 'MULTIPLE_CHOICE', NOW(), NULL),
(198, 22, 1, 'PREG-198', '¿Ejecutas pruebas automáticas para validar configuraciones de infraestructura antes de aplicarlas?', 'Testing de infraestructura.', 'SINGLE_CHOICE', NOW(), NULL),
(199, 23, 1, 'PREG-199', '¿Cómo prevenís y resolvés bloqueos de estado (State Locks) en backends remotos de Terraform?', 'Bloqueos de estado IaC.', 'MULTIPLE_CHOICE', NOW(), NULL),
(200, 24, 1, 'PREG-200', '¿Configurás monitoreo sintético periódico para validar los flujos de usuario más importantes?', 'Monitoreo sintético.', 'SINGLE_CHOICE', NOW(), NULL),
(201, 25, 1, 'PREG-201', '¿Cómo gestionás el escaneo automático de vulnerabilidades en imágenes de contenedores Docker?', 'Escaneo de contenedores.', 'MULTIPLE_CHOICE', NOW(), NULL),
(202, 26, 1, 'PREG-202', '¿Configurás encabezados de seguridad HTTP (CSP, HSTS, X-Frame-Options) en tus aplicaciones web?', 'Encabezados de seguridad.', 'SINGLE_CHOICE', NOW(), NULL),
(203, 27, 1, 'PREG-203', '¿Cómo gestionás y restringís los permisos de las cuentas de servicio en entornos cloud?', 'Cuentas de servicio.', 'MULTIPLE_CHOICE', NOW(), NULL),
(204, 28, 1, 'PREG-204', '¿Analizás costos y tiempos de ejecución de consultas utilizando la herramienta EXPLAIN?', 'Optimización de planes SQL.', 'SINGLE_CHOICE', NOW(), NULL),
(205, 29, 1, 'PREG-205', '¿Ajustás parámetros de memoria como shared_buffers y work_mem según la carga del servidor PostgreSQL?', 'Afinamiento de PostgreSQL.', 'MULTIPLE_CHOICE', NOW(), NULL),
(206, 30, 1, 'PREG-206', '¿Implementás mecanismos de transacciones en dos fases (2PC) o compensaciones cuando es necesario?', 'Protocolos transaccionales.', 'SINGLE_CHOICE', NOW(), NULL),
(207, 31, 1, 'PREG-207', '¿Realizás análisis comparativo de la competencia para resaltar las fortalezas de tu solución?', 'Análisis competitivo.', 'MULTIPLE_CHOICE', NOW(), NULL),
(208, 32, 1, 'PREG-208', '¿Utilizás marcos de trabajo como MEDDIC para calificar acuerdos comerciales de gran envergadura?', 'Metodología de ventas.', 'SINGLE_CHOICE', NOW(), NULL),
(209, 33, 1, 'PREG-209', '¿Cómo negociás cláusulas de responsabilidad e indemnización en contratos con clientes corporativos?', 'Cláusulas contractuales.', 'MULTIPLE_CHOICE', NOW(), NULL),
(210, 34, 1, 'PREG-210', '¿Calculás la capacidad del equipo contemplando feriados, licencias y guardias operativas?', 'Cálculo de capacidad.', 'SINGLE_CHOICE', NOW(), NULL),
(211, 35, 1, 'PREG-211', '¿Cómo hacés seguimiento de la exposición al riesgo en proyectos de alta complejidad técnica?', 'Exposición al riesgo.', 'MULTIPLE_CHOICE', NOW(), NULL),
(212, 36, 1, 'PREG-212', '¿Monitoreás diagramas de flujo acumulado (CFD) para identificar cuellos de botella en el proceso?', 'Diagrama de flujo acumulado.', 'SINGLE_CHOICE', NOW(), NULL),
(213, 37, 1, 'PREG-213', '¿Cómo sintetizás notas de entrevistas cualitativas en patrones y temas clave de oportunidad?', 'Síntesis de investigación.', 'MULTIPLE_CHOICE', NOW(), NULL),
(214, 38, 1, 'PREG-214', '¿Diseñás microinteracciones y animaciones sutiles para enriquecer la experiencia de usuario?', 'Microinteracciones UI.', 'SINGLE_CHOICE', NOW(), NULL),
(215, 39, 1, 'PREG-215', '¿Cómo gobernas y aprobás contribuciones de nuevos componentes al sistema de diseño?', 'Gobernanza de diseño.', 'MULTIPLE_CHOICE', NOW(), NULL),
(216, 40, 1, 'PREG-216', '¿Aplicás técnicas como SMOTE para balancear clases en problemas de clasificación desbalanceados?', 'Balanceo de datasets.', 'SINGLE_CHOICE', NOW(), NULL),
(217, 41, 1, 'PREG-217', '¿Cómo medís la latencia y relevancia en la recuperación de documentos de bases vectoriales?', 'Evaluación vectorial.', 'MULTIPLE_CHOICE', NOW(), NULL),
(218, 42, 1, 'PREG-218', '¿Analizás curvas ROC-AUC y matrices de confusión para evaluar el rendimiento de modelos?', 'Curvas ROC-AUC.', 'SINGLE_CHOICE', NOW(), NULL),
(219, 43, 1, 'PREG-219', '¿Automatizás la validación de esquemas JSON / OpenAPI en cada ejecución del pipeline CI?', 'Validación de contratos.', 'MULTIPLE_CHOICE', NOW(), NULL),
(220, 44, 1, 'PREG-220', '¿Ejecutás pruebas de regresión visual automáticas para detectar cambios no deseados en la interfaz?', 'Regresión visual.', 'SINGLE_CHOICE', NOW(), NULL);

-- ---------------------------------------------------------------------
-- 7. OPCIONES (880 Opciones - Exactamente 4 por pregunta)
--    PESOS (WEIGHTS): SUMA POR PREGUNTA = 10 PUNTOS
--    - SINGLE_CHOICE: 0, 0, 0, 10
--    - MULTIPLE_CHOICE: 1, 2, 3, 4
-- ---------------------------------------------------------------------
INSERT INTO opciones (id, pregunta_id, orden_visualizacion, ponderacion, texto)
SELECT 
    (q.id - 1) * 4 + o_idx AS id,
    q.id AS pregunta_id,
    o_idx AS orden_visualizacion,
    CASE 
        WHEN q.tipo = 'SINGLE_CHOICE' THEN CASE WHEN o_idx = 4 THEN 10 ELSE 0 END
        ELSE o_idx
    END AS ponderacion,
    CASE o_idx
        WHEN 1 THEN 'Respuesta insatisfactoria o criterio mínimo de desempeño.'
        WHEN 2 THEN 'Respuesta básica con margen considerable de mejora.'
        WHEN 3 THEN 'Respuesta adecuada y alineada a buenas prácticas de la industria.'
        WHEN 4 THEN 'Respuesta óptima, destacada y de máximo valor profesional.'
    END AS texto
FROM preguntas q
CROSS JOIN (SELECT 1 AS o_idx UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) opts
ORDER BY q.id, o_idx;

-- ---------------------------------------------------------------------
-- 8. PUESTO_COMPETENCIAS (Relación Puesto - Competencias Requeridas)
-- ---------------------------------------------------------------------
INSERT INTO puesto_competencias (puesto_id, competencia_id, ponderacion_requerida) VALUES
-- Puesto 1: Analista de Datos Junior
(1, 2, 8), (1, 3, 6), (1, 5, 6),

-- Puesto 2: Líder de Equipo de Desarrollo
(2, 1, 8), (2, 5, 6), (2, 6, 6),

-- Puesto 3: Desarrollador Backend Senior
(3, 4, 9), (3, 7, 8), (3, 10, 7),

-- Puesto 4: Ejecutivo de Cuentas Corporativas
(4, 5, 8), (4, 11, 8), (4, 17, 6),

-- Puesto 5: Tech Lead
(5, 1, 7), (5, 2, 7), (5, 4, 8), (5, 7, 8),

-- Puestos 6 al 30
(6, 6, 8), (6, 12, 8), (6, 3, 7),
(7, 1, 6), (7, 6, 6), (7, 12, 8),
(8, 8, 8), (8, 16, 8), (8, 9, 7),
(9, 15, 9), (9, 4, 6), (9, 16, 7),
(10, 13, 8), (10, 18, 8), (10, 5, 6),
(11, 9, 9), (11, 16, 6), (11, 8, 7),
(12, 10, 8), (12, 3, 7), (12, 4, 7),
(13, 3, 8), (13, 11, 7), (13, 12, 8),
(14, 5, 8), (14, 6, 7), (14, 17, 7),
(15, 4, 8), (15, 2, 8), (15, 5, 6),
(16, 7, 9), (16, 8, 8), (16, 9, 7),
(17, 14, 9), (17, 3, 7), (17, 10, 6),
(18, 11, 8), (18, 17, 8), (18, 5, 7),
(19, 8, 9), (19, 16, 8), (19, 9, 7),
(20, 3, 8), (20, 5, 7), (20, 12, 7),
(21, 4, 8), (21, 13, 8), (21, 6, 6),
(22, 17, 8), (22, 5, 7), (22, 16, 6),
(23, 1, 8), (23, 12, 8), (23, 11, 7),
(24, 6, 8), (24, 11, 7), (24, 18, 7),
(25, 9, 9), (25, 16, 8), (25, 7, 6),
(26, 5, 8), (26, 18, 8), (26, 17, 6),
(27, 10, 9), (27, 8, 7), (27, 16, 7),
(28, 4, 8), (28, 13, 8), (28, 6, 6),
(29, 9, 8), (29, 3, 7), (29, 12, 7),
(30, 14, 8), (30, 8, 8), (30, 4, 7);

-- ---------------------------------------------------------------------
-- 9. CANDIDATOS (Semilla inicial de prueba)
-- ---------------------------------------------------------------------
INSERT INTO candidatos (id, numero_candidato, tipo_documento, numero_documento, nombre, apellido, fecha_nacimiento, genero, email, escolaridad, nacionalidad) VALUES
(1, 10001, 'DNI', '38450123', 'Lucas', 'Gimenez', '1994-05-12', 'H', 'lucas.gimenez@email.com', 'Universitario Completo', 'Argentina'),
(2, 10002, 'DNI', '40123987', 'Sofia', 'Rodriguez', '1997-09-24', 'M', 'sofia.rodriguez@email.com', 'Universitario Completo', 'Argentina'),
(3, 10003, 'DNI', '35987654', 'Martin', 'Alvarez', '1991-03-18', 'H', 'martin.alvarez@email.com', 'Universitario Completo', 'Argentina'),
(4, 10004, 'DNI', '42345678', 'Camila', 'Fernandez', '1999-11-05', 'M', 'camila.fernandez@email.com', 'Terciario Completo', 'Argentina'),
(5, 10005, 'DNI', '39876543', 'Federico', 'Diaz', '1996-07-30', 'H', 'federico.diaz@email.com', 'Universitario En Curso', 'Argentina'),
(6, 10006, 'DNI', '37654321', 'Valentina', 'Benitez', '1993-12-14', 'M', 'valentina.benitez@email.com', 'Universitario Completo', 'Argentina'),
(7, 10007, 'DNI', '41234567', 'Gonzalo', 'Perez', '1998-02-28', 'H', 'gonzalo.perez@email.com', 'Secundario Completo', 'Argentina'),
(8, 10008, 'DNI', '36543210', 'Mariana', 'Castro', '1992-08-19', 'M', 'mariana.castro@email.com', 'Posgrado / Master', 'Argentina'),
(9, 10009, 'DNI', '43210987', 'Joaquin', 'Romero', '2000-04-10', 'H', 'joaquin.romero@email.com', 'Universitario En Curso', 'Argentina'),
(10, 10010, 'DNI', '39123456', 'Lucia', 'Herrera', '1995-10-22', 'M', 'lucia.herrera@email.com', 'Universitario Completo', 'Argentina');

-- ---------------------------------------------------------------------
-- 10. CONSULTORES (Mapeados con el directorio LDAP)
-- ---------------------------------------------------------------------
INSERT INTO consultores (id, nombre_usuario) VALUES
(1, 'mfrank'),
(2, 'jperez'),
(3, 'agomez'),
(4, 'lmartinez');

-- ---------------------------------------------------------------------
-- 11. RESINCRONIZACIÓN DE SECUENCIAS (PostgreSQL)
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('empresas', 'id'), COALESCE((SELECT MAX(id) FROM empresas), 1));
SELECT setval(pg_get_serial_sequence('puestos', 'id'), COALESCE((SELECT MAX(id) FROM puestos), 1));
SELECT setval(pg_get_serial_sequence('competencias', 'id'), COALESCE((SELECT MAX(id) FROM competencias), 1));
SELECT setval(pg_get_serial_sequence('factores', 'id'), COALESCE((SELECT MAX(id) FROM factores), 1));
SELECT setval(pg_get_serial_sequence('preguntas', 'id'), COALESCE((SELECT MAX(id) FROM preguntas), 1));
SELECT setval(pg_get_serial_sequence('opciones', 'id'), COALESCE((SELECT MAX(id) FROM opciones), 1));
SELECT setval(pg_get_serial_sequence('candidatos', 'id'), COALESCE((SELECT MAX(id) FROM candidatos), 1));
SELECT setval(pg_get_serial_sequence('consultores', 'id'), COALESCE((SELECT MAX(id) FROM consultores), 1));
