import { createContext, useContext, useState, useEffect, useCallback, useMemo } from 'react';
import { authService } from '../services/auth';
import { ROLES } from '../constants';

export const AuthContext = createContext(null);

export function ProveedorAuth({ children }) {
    const [usuario, setUsuario] = useState(null);
    const [cargando, setCargando] = useState(true);

    const obtenerUsuario = useCallback(async () => {
        try {
            const data = await authService.obtenerPerfil();
            setUsuario(data);
            return data;
        } catch (error) {
            setUsuario(null);
            if (error.response?.status !== 401 && error.response?.status !== 403) {
                console.error('Error al verificar sesión:', error);
            }
            return null;
        }
    }, []);

    const iniciarSesion = useCallback(async (nombreUsuario, contrasenia) => {
        setCargando(true);
        try {
            await authService.iniciarSesion(nombreUsuario, contrasenia);
            await obtenerUsuario();
            return true;
        } catch (error) {
            setUsuario(null);
            throw error;
        } finally {
            setCargando(false);
        }
    }, [obtenerUsuario]);

    const iniciarSesionCandidato = useCallback(async (claveAcceso) => {
        setCargando(true);
        try {
            const respuesta = await authService.iniciarSesionCandidato(claveAcceso);
            await obtenerUsuario();
            return respuesta;
        } catch (error) {
            setUsuario(null);
            throw error;
        } finally {
            setCargando(false);
        }
    }, [obtenerUsuario]);

    const cerrarSesion = useCallback(async () => {
        setCargando(true);
        try {
            await authService.cerrarSesion();
            setUsuario(null);
            return true;
        } catch (error) {
            console.error('Error al cerrar sesión:', error);
            return false;
        } finally {
            setCargando(false);
        }
    }, []);

    useEffect(() => {
        const RUTAS_PUBLICAS = ['/login', '/iniciar-sesion', '/'];
        const esRutaPublica = RUTAS_PUBLICAS.includes(window.location.pathname);

        const inicializar = async () => {
            if (esRutaPublica) {
                setCargando(false);
                return;
            }
            await obtenerUsuario();
            setCargando(false);
        };

        inicializar();
    }, [obtenerUsuario]);

    const rolUsuario = usuario?.rol || usuario?.role;

    const valor = useMemo(() => ({
        usuario,
        cargando,
        iniciarSesion,
        iniciarSesionCandidato,
        cerrarSesion,
        obtenerUsuario,
        estaAutenticado: Boolean(usuario),
        esConsultor: rolUsuario === ROLES.CONSULTOR,
        esCandidato: rolUsuario === ROLES.CANDIDATO,
    }), [usuario, cargando, rolUsuario, iniciarSesion, iniciarSesionCandidato, cerrarSesion, obtenerUsuario]);

    return (
        <AuthContext.Provider value={valor}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth debe usarse dentro de un ProveedorAuth');
    }
    return context;
};

export default AuthContext;
