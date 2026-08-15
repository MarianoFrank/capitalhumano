import { Outlet, Navigate } from 'react-router-dom';
import { useAutenticacion } from '../context/ContextoApp';

export default function RutaProtegida({ requerirConsultor = false, requireConsultant = false }) {
    const { estaAutenticado, esConsultor, cargando } = useAutenticacion();

    if (cargando) {
        return (
            <div className="flex align-items-center justify-content-center min-h-screen">
                <i className="pi pi-spin pi-spinner text-4xl text-primary"></i>
            </div>
        );
    }

    // Si no está autenticado, redirigir al login
    if (!estaAutenticado) {
        return <Navigate to="/iniciar-sesion" replace />;
    }

    // Si requiere rol consultor y no lo es, redirigir a inicio
    const necesitaConsultor = requerirConsultor || requireConsultant;
    if (necesitaConsultor && !esConsultor) {
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
}

export const ProtectedRoute = RutaProtegida;
