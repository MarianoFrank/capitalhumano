import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Button } from 'primereact/button';
import { ProgressBar } from 'primereact/progressbar';
import { apiCapitalHumano } from '../../config/api';
import { useToast } from '../../context/ContextoApp';
import BloquePreguntas from './BloquePreguntas';
import Temporizador from './Temporizador';

export default function AsistenteCuestionario() {
    const { id } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const { mostrarError, mostrarExito } = useToast();

    const [cargando, setCargando] = useState(true);
    const [numeroBloqueActual, setNumeroBloqueActual] = useState(1);
    const [totalBloques, setTotalBloques] = useState(1);
    const [datosBloqueActual, setDatosBloqueActual] = useState(null);
    const [respuestas, setRespuestas] = useState({});

    const [duracionMinutos, setDuracionMinutos] = useState(0);
    const [fechaInicio, setFechaInicio] = useState(null);

    // Manejador si el cuestionario ya finalizó o hubo error de estado
    const manejarErrorCritico = useCallback((error) => {
        const mensajeError = error.response?.data?.error || error.response?.data?.message || 'Error inesperado.';
        const msgMin = mensajeError.toLowerCase();

        if (['habilitado', 'curso', 'finalizado', 'estado', 'unidireccional', 'orden'].some(palabra => msgMin.includes(palabra))) {
            navigate(`/cuestionario/${id}/completado`, { replace: true });
        } else {
            mostrarError(mensajeError);
            setCargando(false);
        }
    }, [id, navigate, mostrarError]);

    // Función para traer la data de un bloque
    const cargarBloque = useCallback(async (numBloque) => {
        const bloqueDestino = (numBloque != null && numBloque > 0) ? numBloque : 1;
        setCargando(true);
        try {
            const { data } = await apiCapitalHumano.get(`/api/cuestionarios/${id}/bloques/${bloqueDestino}`);
            setDatosBloqueActual(data);
            setRespuestas({});
        } catch (error) {
            manejarErrorCritico(error);
        } finally {
            setCargando(false);
        }
    }, [id, manejarErrorCritico]);

    // Inicialización del cuestionario
    useEffect(() => {
        const inicializarAsistente = async () => {
            setCargando(true);
            try {
                let bloqueACargar = location.state?.bloqueActual ?? location.state?.currentBlock;
                let total = location.state?.totalBloques ?? location.state?.totalBlocks;
                let duracion = location.state?.duracionMinutos ?? location.state?.durationMinutes;
                let inicio = location.state?.fechaInicio ?? location.state?.startedAt;

                // Fallback si recargan la página o si no vino en el estado
                if (bloqueACargar == null || total == null || inicio == null) {
                    const { data } = await apiCapitalHumano.post(`/api/cuestionarios/${id}/iniciar`);
                    bloqueACargar = data.bloqueActual ?? data.currentBlock ?? 1;
                    total = data.totalBloques ?? data.totalBlocks ?? 1;
                    duracion = data.duracionMinutos ?? data.durationMinutes ?? 60;
                    inicio = data.fechaInicio ?? data.startedAt;
                }

                const bloqueFinal = (bloqueACargar != null && Number(bloqueACargar) > 0) ? Number(bloqueACargar) : 1;
                const totalFinal = (total != null && Number(total) > 0) ? Number(total) : 1;

                setTotalBloques(totalFinal);
                setNumeroBloqueActual(bloqueFinal);
                setDuracionMinutos(duracion || 60);
                setFechaInicio(inicio);

                await cargarBloque(bloqueFinal);
            } catch (error) {
                manejarErrorCritico(error);
            }
        };

        inicializarAsistente();
    }, [id, location.state, cargarBloque, manejarErrorCritico]);

    const manejarSeleccionOpcion = (idItemPregunta, idOpcion) => {
        setRespuestas(prev => ({ ...prev, [idItemPregunta]: idOpcion }));
    };

    const manejarSiguienteBloque = async () => {
        const preguntasEnBloque = datosBloqueActual?.itemsPregunta || datosBloqueActual?.questionItems || [];
        const payload = {};

        for (const item of preguntasEnBloque) {
            const resp = respuestas[item.id];
            if (Array.isArray(resp) ? resp.length > 0 : resp != null) {
                payload[item.id] = Array.isArray(resp) ? resp : [resp];
            }
        }

        if (Object.keys(payload).length < preguntasEnBloque.length) {
            mostrarError('Por favor, respondé todas las preguntas antes de avanzar.');
            return;
        }

        setCargando(true);
        try {
            await apiCapitalHumano.post(`/api/cuestionarios/${id}/bloques/${numeroBloqueActual}/responder`, payload);

            if (numeroBloqueActual < totalBloques) {
                const siguiente = numeroBloqueActual + 1;
                setNumeroBloqueActual(siguiente);
                await cargarBloque(siguiente);
                window.scrollTo({ top: 0, behavior: 'smooth' });
            } else {
                mostrarExito('¡Evaluación finalizada con éxito!');
                navigate(`/cuestionario/${id}/completado`, { replace: true });
            }
        } catch (error) {
            manejarErrorCritico(error);
        }
    };

    const manejarTiempoCumplido = () => {
        mostrarError('¡Se acabó el tiempo! Tu evaluación será cerrada.');
        navigate(`/cuestionario/${id}/completado`, { replace: true });
    };

    const porcentajeProgreso = totalBloques > 0 ? (numeroBloqueActual / totalBloques) * 100 : 0;

    return (
        <div className="surface-card border-1 surface-border border-round p-4 sm:p-6 shadow-2 fadein mt-3">
            <div className="flex flex-column gap-2 mb-5">
                <div className="flex justify-content-between align-items-center">
                    <span className="font-bold text-sm text-primary">
                        Bloque {numeroBloqueActual} de {totalBloques}
                    </span>
                    <Temporizador fechaInicio={fechaInicio} duracionMinutos={duracionMinutos} alTerminarTiempo={manejarTiempoCumplido} />
                </div>
                <ProgressBar value={porcentajeProgreso} showValue={false} style={{ height: '8px' }} />
            </div>

            {cargando ? (
                <div className="flex justify-content-center align-items-center py-6">
                    <i className="pi pi-spin pi-spinner text-4xl text-primary"></i>
                </div>
            ) : datosBloqueActual ? (
                <BloquePreguntas
                    bloque={datosBloqueActual}
                    respuestas={respuestas}
                    alSeleccionarOpcion={manejarSeleccionOpcion}
                />
            ) : null}

            <div className="mt-6 pt-4 border-top-1 surface-border flex justify-content-end">
                <Button
                    label={numeroBloqueActual === totalBloques ? 'Finalizar Evaluación' : 'Siguiente Bloque'}
                    icon={numeroBloqueActual === totalBloques ? 'pi pi-check' : 'pi pi-arrow-right'}
                    iconPos="right"
                    severity={numeroBloqueActual === totalBloques ? 'success' : 'primary'}
                    onClick={manejarSiguienteBloque}
                    disabled={cargando}
                />
            </div>
        </div>
    );
}

export const QuestionnaireWizard = AsistenteCuestionario;
