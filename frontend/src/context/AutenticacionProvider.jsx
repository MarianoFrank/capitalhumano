import { useState, useEffect, useCallback } from 'react';
import { apiCapitalHumano } from '../config/api';
import { AutenticacionContext } from './AutenticacionContext';

export default function AutenticacionProvider({ children }) {
    const [usuario, setUsuario] = useState(null);
    const [cargando, setCargando] = useState(true);

    const obtenerUsuario = useCallback(async () => {
        try {
            const { data } = await apiCapitalHumano.get('/api/autenticacion/perfil');
            setUsuario(data);
            return data;
        } catch (error) {
            setUsuario(null);

            if (error.response?.status !== 403) {
                console.error('Error verificando sesión:', error);
            }

            return null;
        }
    }, []);

    const iniciarSesion = async (nombreUsuario, contrasenia) => {
        setCargando(true);

        try {
            await apiCapitalHumano.post('/api/autenticacion/iniciar-sesion', {
                usuario: nombreUsuario,
                contrasenia: contrasenia
            });

            // Después del login recuperamos el usuario
            await obtenerUsuario();
            return true;

        } catch (error) {
            setUsuario(null);
            throw error;

        } finally {
            setCargando(false);
        }
    };

    const iniciarSesionCandidato = async (claveAcceso) => {
        setCargando(true);
        try {
            const response = await apiCapitalHumano.post('/api/autenticacion/candidato/iniciar-sesion', { 
                accessCode: claveAcceso,
                claveAcceso: claveAcceso
            });
            // Después de loguear con éxito, actualizamos perfil
            await obtenerUsuario();

            return response.data;
        } catch (error) {
            setUsuario(null);
            throw error;
        } finally {
            setCargando(false);
        }
    };

    const cerrarSesion = async () => {
        setCargando(true);

        try {
            await apiCapitalHumano.post('/api/autenticacion/cerrar-sesion');
            setUsuario(null);
            return true;

        } catch (error) {
            console.error('Error cerrando sesión:', error);
            return false;

        } finally {
            setCargando(false);
        }
    };

    useEffect(() => {
        const RUTAS_PUBLICAS = [
            '/login',
            '/iniciar-sesion',
            '/register',
            '/'
        ];

        const inicializarAutenticacion = async () => {
            const esRutaPublica = RUTAS_PUBLICAS.includes(window.location.pathname);

            if (esRutaPublica) {
                setCargando(false);
                return;
            }

            await obtenerUsuario();
            setCargando(false);
        };

        inicializarAutenticacion();
    }, [obtenerUsuario]);

    const valorContexto = {
        // En español
        usuario,
        cargando,
        iniciarSesion,
        iniciarSesionCandidato,
        cerrarSesion,
        estaAutenticado: Boolean(usuario),
        esConsultor: usuario?.role === 'CONSULTANT' || usuario?.rol === 'CONSULTANT',
        esCandidato: usuario?.role === 'CANDIDATE' || usuario?.rol === 'CANDIDATE',

        // Alias React/App
        user: usuario,
        loading: cargando,
        login: iniciarSesion,
        loginCandidate: iniciarSesionCandidato,
        logout: cerrarSesion,
        isAuthenticated: Boolean(usuario),
        isConsultant: usuario?.role === 'CONSULTANT' || usuario?.rol === 'CONSULTANT',
        isCandidate: usuario?.role === 'CANDIDATE' || usuario?.rol === 'CANDIDATE'
    };

    return (
        <AutenticacionContext.Provider value={valorContexto}>
            {children}
        </AutenticacionContext.Provider>
    );
}

export const AuthProvider = AutenticacionProvider;
