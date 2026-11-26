import { ProveedorTema, useTema } from './TemaContext';
import { ProveedorToast, useToast } from './ToastContext';
import { ProveedorAuth, useAuth } from './AuthContext';

export { useTema } from './TemaContext';
export { useToast } from './ToastContext';
export { useAuth } from './AuthContext';

/**
 * Proveedor global de la aplicación que compone Tema, Toast y Autenticación.
 */
export function ProveedorApp({ children }) {
    return (
        <ProveedorTema>
            <ProveedorToast>
                <ProveedorAuth>
                    {children}
                </ProveedorAuth>
            </ProveedorToast>
        </ProveedorTema>
    );
}

export default ProveedorApp;
