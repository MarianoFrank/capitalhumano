import { Routes, Route, Navigate } from 'react-router-dom';
import RutaProtegida from './guards/RutaProtegida';

// Layouts
import DisenioConsultor from './pages/consultor/DisenioConsultor';
import DisenioCandidato from './pages/candidato/DisenioCandidato';

// Páginas Públicas y Autenticación
import InicioPublico from './pages/InicioPublico';
import IniciarSesion from './pages/autenticacion/IniciarSesion';
import NoEncontrado from './pages/errores/NoEncontrado';

// Zona Consultor
import BienvenidaConsultor from './pages/consultor/BienvenidaConsultor';
import GenerarEvaluacion from './pages/consultor/GenerarEvaluacion';
import ListaPreguntas from './pages/consultor/preguntas/ListaPreguntas';
import CrearPregunta from './pages/consultor/preguntas/CrearPregunta';
import ListaOrdenMerito from './pages/consultor/reportes/ListaOrdenMerito';
import ReporteOrdenMerito from './pages/consultor/reportes/ReporteOrdenMerito';

// Zona Candidato
import Instrucciones from './pages/candidato/Instrucciones';
import AsistenteCuestionario from './pages/candidato/AsistenteCuestionario';
import EvaluacionCompletada from './pages/candidato/EvaluacionCompletada';

function App() {
    return (
        <Routes>
            {/* --- RUTAS PÚBLICAS --- */}
            <Route path="/" element={<InicioPublico />} />
            <Route path="/iniciar-sesion" element={<IniciarSesion />} />
            <Route path="/login" element={<IniciarSesion />} />

            {/* --- ZONA CONSULTOR (PROTEGIDA) --- */}
            <Route element={<RutaProtegida requerirConsultor={true} />}>
                <Route element={<DisenioConsultor />}>
                    <Route path="/inicio" element={<BienvenidaConsultor />} />
                    <Route path="/dashboard" element={<BienvenidaConsultor />} />

                    <Route path="/evaluar" element={<GenerarEvaluacion />} />
                    <Route path="/evaluate" element={<GenerarEvaluacion />} />

                    {/* Gestión de Preguntas */}
                    <Route path="/preguntas" element={<ListaPreguntas />} />
                    <Route path="/questions" element={<ListaPreguntas />} />
                    <Route path="/preguntas/nueva" element={<CrearPregunta />} />
                    <Route path="/questions/new" element={<CrearPregunta />} />

                    {/* Reportes */}
                    <Route path="/reportes/orden-merito" element={<ListaOrdenMerito />} />
                    <Route path="/reports/merit-order" element={<ListaOrdenMerito />} />
                    <Route path="/reportes/orden-merito/:id" element={<ReporteOrdenMerito />} />
                    <Route path="/reports/merit-order/:id" element={<ReporteOrdenMerito />} />
                </Route>
            </Route>

            {/* --- ZONA CANDIDATO --- */}
            <Route path="/cuestionario" element={<DisenioCandidato />}>
                <Route path=":id" element={<Instrucciones />} />
                <Route path=":id/evaluacion" element={<AsistenteCuestionario />} />
                <Route path=":id/test" element={<AsistenteCuestionario />} />
                <Route path=":id/completado" element={<EvaluacionCompletada />} />
                <Route path=":id/completed" element={<EvaluacionCompletada />} />
            </Route>

            <Route path="/questionnaire" element={<DisenioCandidato />}>
                <Route path=":id" element={<Instrucciones />} />
                <Route path=":id/evaluacion" element={<AsistenteCuestionario />} />
                <Route path=":id/test" element={<AsistenteCuestionario />} />
                <Route path=":id/completado" element={<EvaluacionCompletada />} />
                <Route path=":id/completed" element={<EvaluacionCompletada />} />
            </Route>

            {/* --- RUTA 404 --- */}
            <Route path="*" element={<NoEncontrado />} />
        </Routes>
    );
}

export default App;
