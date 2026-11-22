import { Routes, Route, Navigate } from 'react-router-dom';

// Layouts y Guards
import RutaProtegida from './components/RutaProtegida';
import DisenioConsultor from './components/DisenioConsultor';
import DisenioCandidato from './components/DisenioCandidato';

// Vistas Públicas y Autenticación
import InicioPublico from './views/auth/InicioPublico';
import IniciarSesion from './views/auth/IniciarSesion';
import NoEncontrado from './views/auth/NoEncontrado';

// Vistas del Consultor
import BienvenidaConsultor from './views/consultor/BienvenidaConsultor';
import GenerarEvaluacion from './views/consultor/evaluar/GenerarEvaluacion';
import ListaPreguntas from './views/consultor/preguntas/ListaPreguntas';
import CrearPregunta from './views/consultor/preguntas/CrearPregunta';
import ListaOrdenMerito from './views/consultor/reportes/ListaOrdenMerito';
import ReporteOrdenMerito from './views/consultor/reportes/ReporteOrdenMerito';

// Vistas del Candidato
import Instrucciones from './views/candidato/Instrucciones';
import AsistenteCuestionario from './views/candidato/AsistenteCuestionario';
import EvaluacionCompletada from './views/candidato/EvaluacionCompletada';

export default function App() {
    return (
        <Routes>
            {/* Rutas Públicas */}
            <Route path="/" element={<InicioPublico />} />
            <Route path="/iniciar-sesion" element={<IniciarSesion />} />

            {/* Zona Consultor (Protegida) */}
            <Route element={<RutaProtegida requerirConsultor={true} />}>
                <Route element={<DisenioConsultor />}>
                    <Route path="/inicio" element={<BienvenidaConsultor />} />
                    <Route path="/evaluar" element={<GenerarEvaluacion />} />
                    <Route path="/preguntas" element={<ListaPreguntas />} />
                    <Route path="/preguntas/nueva" element={<CrearPregunta />} />
                    <Route path="/reportes/orden-merito" element={<ListaOrdenMerito />} />
                    <Route path="/reportes/orden-merito/:id" element={<ReporteOrdenMerito />} />
                </Route>
            </Route>

            {/* Zona Candidato */}
            <Route path="/cuestionario" element={<DisenioCandidato />}>
                <Route path=":id" element={<Instrucciones />} />
                <Route path=":id/evaluacion" element={<AsistenteCuestionario />} />
                <Route path=":id/completado" element={<EvaluacionCompletada />} />
            </Route>

            {/* Redirecciones de Compatibilidad */}
            <Route path="/login" element={<Navigate to="/iniciar-sesion" replace />} />
            <Route path="/dashboard" element={<Navigate to="/inicio" replace />} />
            <Route path="/evaluate" element={<Navigate to="/evaluar" replace />} />
            <Route path="/questions" element={<Navigate to="/preguntas" replace />} />
            <Route path="/reports/merit-order" element={<Navigate to="/reportes/orden-merito" replace />} />
            <Route path="/reports/merit-order/:id" element={<Navigate to="/reportes/orden-merito/:id" replace />} />

            {/* Ruta 404 */}
            <Route path="*" element={<NoEncontrado />} />
        </Routes>
    );
}
