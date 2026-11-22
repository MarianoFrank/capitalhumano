import axios from 'axios';

/**
 * Cliente HTTP Axios configurado para la plataforma Capital Humano.
 */
export const api = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Interceptor global para redirección en caso de sesión de candidato expirada
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const rutaActual = window.location.pathname;
            if (rutaActual.startsWith('/cuestionario') || rutaActual.startsWith('/questionnaire')) {
                window.location.href = '/?expirado=true';
            }
        }
        return Promise.reject(error);
    }
);

export default api;
