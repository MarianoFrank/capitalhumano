import { Routes, Route, Navigate } from "react-router-dom";
import AutenticacionProvider from "./context/AutenticacionProvider";
import RutaProtegida from "./guards/RutaProtegida";
import DashboardLayout from "./pages/consultant/DashboardLayout";

import IniciarSesion from "./pages/auth/Login";
import { NotFound } from "./pages/errors/NotFound";
import BienvenidaConsultor from "./pages/consultant/DashboardWelcome";
import InicioPublico from "./pages/PublicHome";
import QuestionList from "./pages/consultant/questions/QuestionList";
import CreateQuestion from "./pages/consultant/questions/CreateQuestion";
import GenerateEvaluation from "./pages/consultant/GenerateEvaluation";
import CandidatoLayout from "./pages/candidate/CandidateLayout";
import Instructions from "./pages/candidate/Instructions";
import QuestionnaireWizard from "./pages/candidate/QuestionnaireWizard";
import TestCompleted from "./pages/candidate/TestCompleted";
import MeritOrderList from "./pages/consultant/reports/MeritOrderList";
import MeritOrderReport from "./pages/consultant/reports/MeritOrderReport";

function App() {
    return (
        <AutenticacionProvider>
            <Routes>
                {/* --- RUTAS PÚBLICAS --- */}
                <Route path="/login" element={<IniciarSesion />} />
                <Route path="/iniciar-sesion" element={<IniciarSesion />} />
                <Route path="/" element={<InicioPublico />} />

                {/* --- ZONA CONSULTOR --- */}
                <Route element={<RutaProtegida requerirConsultor={true} />}>
                    <Route element={<DashboardLayout />}>
                        <Route path="/dashboard" element={<BienvenidaConsultor />} />
                        <Route path="/inicio" element={<BienvenidaConsultor />} />

                        <Route path="/evaluar" element={<GenerateEvaluation />} />
                        <Route path="/evaluate" element={<GenerateEvaluation />} />

                        {/* Gestión de preguntas */}
                        <Route path="/preguntas/nueva" element={<CreateQuestion />} />
                        <Route path="/questions/new" element={<CreateQuestion />} />
                        <Route path="/preguntas" element={<QuestionList />} />
                        <Route path="/questions" element={<QuestionList />} />

                        {/* Reportes */}
                        <Route path="/reportes/orden-merito" element={<MeritOrderList />} />
                        <Route path="/reports/merit-order" element={<MeritOrderList />} />
                        <Route path="/reportes/orden-merito/:id" element={<MeritOrderReport />} />
                        <Route path="/reports/merit-order/:id" element={<MeritOrderReport />} />
                    </Route>
                </Route>

                {/* --- ZONA CANDIDATO --- */}
                <Route path="/cuestionario" element={<CandidatoLayout />}>
                    <Route path=":id" element={<Instructions />} />
                    <Route path=":id/evaluacion" element={<QuestionnaireWizard />} />
                    <Route path=":id/test" element={<QuestionnaireWizard />} />
                    <Route path=":id/completado" element={<TestCompleted />} />
                    <Route path=":id/completed" element={<TestCompleted />} />
                </Route>

                <Route path="/questionnaire" element={<CandidatoLayout />}>
                    <Route path=":id" element={<Instructions />} />
                    <Route path=":id/evaluacion" element={<QuestionnaireWizard />} />
                    <Route path=":id/test" element={<QuestionnaireWizard />} />
                    <Route path=":id/completado" element={<TestCompleted />} />
                    <Route path=":id/completed" element={<TestCompleted />} />
                </Route>

                {/* --- RUTA 404 --- */}
                <Route path="*" element={<NotFound />} />
            </Routes>
        </AutenticacionProvider>
    );
}

export default App;
