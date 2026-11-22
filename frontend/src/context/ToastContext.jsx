import { createContext, useContext, useRef, useCallback, useMemo } from 'react';
import { Toast } from 'primereact/toast';

export const ToastContext = createContext(null);

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
        // Alias de compatibilidad
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
        throw new Error('useToast debe usarse dentro de un ProveedorToast');
    }
    return context;
};

export default ToastContext;
