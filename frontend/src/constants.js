/**
 * Constantes de dominio de la plataforma Capital Humano.
 */

// Roles de usuario
export const ROLES = {
    CONSULTOR: 'CONSULTANT',
    CANDIDATO: 'CANDIDATE',
};

// Tipos de pregunta
export const TIPOS_PREGUNTA = {
    UNICA: 'SINGLE_CHOICE',
    MULTIPLE: 'MULTIPLE_CHOICE',
};

export const OPCIONES_TIPO_PREGUNTA = [
    { label: 'Selección única (Single Choice)', value: TIPOS_PREGUNTA.UNICA },
    { label: 'Selección múltiple (Multiple Choice)', value: TIPOS_PREGUNTA.MULTIPLE }
];

// Estados de evaluación de candidatos
export const ESTADOS_EVALUACION = {
    COMPLETADO: 'COMPLETED',
    EN_PROGRESO: 'IN_PROGRESS',
    ACTIVO: 'ACTIVE',
    INCOMPLETO: 'INCOMPLETE',
    NO_RESPONDIDO: 'NOT_ANSWERED',
};

export const ETIQUETAS_ESTADO = {
    [ESTADOS_EVALUACION.COMPLETADO]: 'Completado',
    [ESTADOS_EVALUACION.EN_PROGRESO]: 'En Progreso',
    [ESTADOS_EVALUACION.ACTIVO]: 'No iniciado',
    [ESTADOS_EVALUACION.INCOMPLETO]: 'Incompleto',
    [ESTADOS_EVALUACION.NO_RESPONDIDO]: 'No Respondido',
};

export const COLORES_ESTADO = {
    [ESTADOS_EVALUACION.COMPLETADO]: 'bg-green-100 text-green-800',
    [ESTADOS_EVALUACION.EN_PROGRESO]: 'bg-blue-100 text-blue-800',
    [ESTADOS_EVALUACION.ACTIVO]: 'bg-gray-100 text-gray-800',
    [ESTADOS_EVALUACION.INCOMPLETO]: 'bg-yellow-100 text-yellow-800',
    [ESTADOS_EVALUACION.NO_RESPONDIDO]: 'bg-red-100 text-red-800',
};
