import { createContext, useContext, useState, useEffect, useCallback, useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { apiCapitalHumano } from '../config/api';

// ============================================================================
// 1. CONTEXTO DE TEMA (CLARO / OSCURO)
// ============================================================================
export const TemaContext = createContext(null);

const TEMA_CLARO = 'lara-light-cyan';
const TEMA_OSCURO = 'lara-dark-cyan';

export function ProveedorTema({ children }) {
    const [tema, setTema] = useState(
        () => localStorage.getItem('tema') || localStorage.getItem('theme') || TEMA_CLARO
    );

    useEffect(() => {
        const enlaceTema = document.getElementById('theme-link');
        if (!enlaceTema) {
            console.error('No se encontró <link id="theme-link"> en index.html');
            return;
        }

        enlaceTema.href = `/themes/${tema}/theme.css`;
        localStorage.setItem('tema', tema);
        localStorage.setItem('theme', tema);
    }, [tema]);

    const alternarTema = useCallback(() => {
        setTema((actual) => (actual === TEMA_CLARO ? TEMA_OSCURO : TEMA_CLARO));
    }, []);

    const valor = useMemo(() => ({
        tema,
        esOscuro: tema === TEMA_OSCURO,
        setTema,
        alternarTema,
        // Alias
        theme: tema,
        isDark: tema === TEMA_OSCURO,
        setTheme: setTema,
        toggleTheme: alternarTema
    }), [tema, alternarTema]);

    return (
        <TemaContext.Provider value={valor}>
            {children}
        </TemaContext.Provider>
    );
}

export const useTema = () => {
    const context = useContext(TemaContext);
    if (!context) {
        throw new Error('useTema debe ser usado dentro de un ProveedorTema o ProveedorApp');
    }
    return context;
};
export const useTheme = useTema;


// ============================================================================
// 2. CONTEXTO DE NOTIFICACIONES / TOAST
// ============================================================================
export const ToastContext = createContext(null);
export const NotificacionContext = ToastContext;

export function ProveedorToast({ children }) {
    const toastRef = useRef(null);

    const mostrarExito = useCallback((detalle, resumen = 'Éxito') => {
        toastRef.current?.show({ severity: 'success', summary: resumen, detail: detalle, life: 3500 });
    }, []);

    const mostrarError = useCallback((detalle, resumen = 'Error') => {
        toastRef.current?.show({ severity: 'error', summary: resumen, detail: detalle, life: 4000 });
    }, []);

    const mostrarInfo = useCallback((detalle, resumen = 'Información') => {
        toastRef.current?.show({ severity: 'info', summary: resumen, detail: detalle, life: 3500 });
    }, []);

    const mostrarAdvertencia = useCallback((detalle, resumen = 'Advertencia') => {
        toastRef.current?.show({ severity: 'warn', summary: resumen, detail: detalle, life: 3500 });
    }, []);

    const valor = useMemo(() => ({
        mostrarExito,
        mostrarError,
        mostrarInfo,
        mostrarAdvertencia,
        // Alias compatibles
        showSuccess: mostrarExito,
        showError: mostrarError,
        showInfo: mostrarInfo,
        showWarn: mostrarAdvertencia
    }), [mostrarExito, mostrarError, mostrarInfo, mostrarAdvertencia]);

    return (
        <ToastContext.Provider value={valor}>
            <Toast ref={toastRef} />
            {children}
        </ToastContext.Provider>
    );
}

export const useToast = () => {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error('useToast debe ser usado dentro de un ProveedorToast o ProveedorApp');
    }
    return context;
};
export const useNotificacion = useToast;
export const useAppToast = useToast;


// ============================================================================
// 3. CONTEXTO DE AUTENTICACIÓN
// ============================================================================
export const AutenticacionContext = createContext(null);

export function ProveedorAutenticacion({ children }) {
    const [usuario, setUsuario] = useState(null);
    const [cargando, setCargando] = useState(true);

    const obtenerUsuario = useCallback(async () => {
        try {
            const { data } = await apiCapitalHumano.get('/api/autenticacion/perfil');
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
            await apiCapitalHumano.post('/api/autenticacion/iniciar-sesion', {
                usuario: nombreUsuario,
                contrasenia: contrasenia
            });
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
            const respuesta = await apiCapitalHumano.post('/api/autenticacion/candidato/iniciar-sesion', {
                claveAcceso: claveAcceso
            });
            await obtenerUsuario();
            return respuesta.data;
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
            await apiCapitalHumano.post('/api/autenticacion/cerrar-sesion');
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
        const RUTAS_PUBLICAS = [
            '/login',
            '/iniciar-sesion',
            '/'
        ];

        const inicializar = async () => {
            const esRutaPublica = RUTAS_PUBLICAS.includes(window.location.pathname);
            if (esRutaPublica) {
                setCargando(false);
                return;
            }
            await obtenerUsuario();
            setCargando(false);
        };

        inicializar();
    }, [obtenerUsuario]);

    const valor = useMemo(() => ({
        usuario,
        cargando,
        iniciarSesion,
        iniciarSesionCandidato,
        cerrarSesion,
        obtenerUsuario,
        estaAutenticado: Boolean(usuario),
        esConsultor: usuario?.rol === 'CONSULTANT' || usuario?.role === 'CONSULTANT',
        esCandidato: usuario?.rol === 'CANDIDATE' || usuario?.role === 'CANDIDATE',

        // Alias
        user: usuario,
        loading: cargando,
        login: iniciarSesion,
        loginCandidate: iniciarSesionCandidato,
        logout: cerrarSesion,
        isAuthenticated: Boolean(usuario),
        isConsultant: usuario?.rol === 'CONSULTANT' || usuario?.role === 'CONSULTANT',
        isCandidate: usuario?.rol === 'CANDIDATE' || usuario?.role === 'CANDIDATE'
    }), [usuario, cargando, iniciarSesion, iniciarSesionCandidato, cerrarSesion, obtenerUsuario]);

    return (
        <AutenticacionContext.Provider value={valor}>
            {children}
        </AutenticacionContext.Provider>
    );
}

export const useAutenticacion = () => {
    const context = useContext(AutenticacionContext);
    if (!context) {
        throw new Error('useAutenticacion debe ser usado dentro de un ProveedorAutenticacion o ProveedorApp');
    }
    return context;
};
export const useAuth = useAutenticacion;


// ============================================================================
// 4. PROVEEDOR GLOBAL COMBINADO
// ============================================================================
export function ProveedorApp({ children }) {
    return (
        <ProveedorTema>
            <ProveedorToast>
                <ProveedorAutenticacion>
                    {children}
                </ProveedorAutenticacion>
            </ProveedorToast>
        </ProveedorTema>
    );
}

export const AppProvider = ProveedorApp;
export default ProveedorApp;
