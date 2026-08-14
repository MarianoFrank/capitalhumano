import { Outlet, Navigate } from 'react-router-dom';
import { useAutenticacion } from '../hooks/useAutenticacion';

export default function RutaProtegida({ requerirConsultor = false, requireConsultant = false }) {
    const { estaAutenticado, esConsultor, cargando } = useAutenticacion();

    if (cargando) return <div className="p-4 text-center text-color-secondary">Cargando sesión...</div>;

    // Si no está autenticado, redirigir al login
    if (!estaAutenticado) return <Navigate to="/login" replace />;

    // Si requiere ser consultor y no lo es, redirigir
    const necesitaConsultor = requerirConsultor || requireConsultant;
    if (necesitaConsultor && !esConsultor) return <Navigate to="/" replace />;

    return <Outlet />;
}

export const ProtectedRoute = RutaProtegida;
