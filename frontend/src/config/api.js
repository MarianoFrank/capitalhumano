import axios from 'axios';

export const apiCapitalHumano = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    withCredentials: true, // Manda y recibe las cookies de sesión
    headers: {
        'Content-Type': 'application/json',
    }
});

// Alias corto y compatibilidad
export const api = apiCapitalHumano;
export const tmApi = apiCapitalHumano;

// Interceptor de respuesta simple
apiCapitalHumano.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            if (window.location.pathname.startsWith('/cuestionario') || window.location.pathname.startsWith('/questionnaire')) {
                window.location.href = '/?expirado=true';
            }
        }
        return Promise.reject(error);
    }
);

export default apiCapitalHumano;
