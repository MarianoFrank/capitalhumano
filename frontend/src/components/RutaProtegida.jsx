import { Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '../context';
import Cargando from './Cargando';

export default function RutaProtegida({ requerirConsultor = false }) {
    const { estaAutenticado, esConsultor, cargando } = useAuth();

    if (cargando) {
        return <Cargando mensaje="Verificando sesión..." />;
    }

    if (!estaAutenticado) {
        return <Navigate to="/iniciar-sesion" replace />;
    }

    if (requerirConsultor && !esConsultor) {
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
}
