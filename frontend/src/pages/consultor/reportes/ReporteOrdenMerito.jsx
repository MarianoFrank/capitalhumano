import { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { apiCapitalHumano } from '../../../config/api';
import { useToast } from '../../../context/ContextoApp';
import { pdf } from '@react-pdf/renderer';
import PdfOrdenMerito from './PdfOrdenMerito';

export const ReporteOrdenMerito = () => {
    const { id: idPuesto } = useParams();
    const [searchParams] = useSearchParams();
    const idEvaluacion = searchParams.get('idEvaluacion') || searchParams.get('evaluationId');

    const { mostrarError, mostrarExito } = useToast();
    const navigate = useNavigate();

    const [datosReporte, setDatosReporte] = useState(null);
    const [cargando, setCargando] = useState(true);
    const [generandoPdf, setGenerandoPdf] = useState(false);

    useEffect(() => {
        const obtenerReporte = async () => {
            try {
                const endpoint = idEvaluacion
                    ? `/api/reportes/orden-merito/${idPuesto}?idEvaluacion=${idEvaluacion}`
                    : `/api/reportes/orden-merito/${idPuesto}`;

                const respuesta = await apiCapitalHumano.get(endpoint);
                setDatosReporte(respuesta.data);
            } catch {
                mostrarError('Error al generar el orden de mérito. Verifique que la evaluación sea correcta.');
            } finally {
                setCargando(false);
            }
        };

        obtenerReporte();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [idPuesto, idEvaluacion]);

    const manejarGenerarPdf = async () => {
        setGenerandoPdf(true);
        try {
            const documento = <PdfOrdenMerito datosReporte={datosReporte} />;
            const blob = await pdf(documento).toBlob();
            const url = URL.createObjectURL(blob);

            const nombreSeguro = (datosReporte.nombrePuesto || '').replace(/[^a-z0-9]/gi, '_');
            const enlace = document.createElement('a');
            enlace.href = url;
            enlace.download = `Orden_Merito_${nombreSeguro}.pdf`;
            document.body.appendChild(enlace);
            enlace.click();
            enlace.remove();
            URL.revokeObjectURL(url);

            mostrarExito('PDF descargado correctamente.');
        } catch {
            mostrarError('Hubo un error al crear el archivo PDF.');
        } finally {
            setGenerandoPdf(false);
        }
    };

    const plantillaCandidato = (fila) => `${fila.apellido}, ${fila.nombre}`;
    const plantillaDocumento = (fila) => `${fila.tipoDocumento} ${fila.numeroDocumento}`;
    const plantillaPuntaje = (fila) => fila.puntaje != null ? fila.puntaje.toFixed(2) : '-';

    const plantillaEstado = (fila) => {
        const insignias = {
            'COMPLETED': 'bg-green-100 text-green-800',
            'INCOMPLETE': 'bg-yellow-100 text-yellow-800',
            'NOT_ANSWERED': 'bg-red-100 text-red-800',
            'IN_PROGRESS': 'bg-blue-100 text-blue-800',
            'ACTIVE': 'bg-gray-100 text-gray-800'
        };

        const traducciones = {
            'COMPLETED': 'Completado',
            'IN_PROGRESS': 'En Progreso',
            'ACTIVE': 'No iniciado',
            'INCOMPLETE': 'Incompleto',
            'NOT_ANSWERED': 'No Respondido'
        };

        const claseColor = insignias[fila.estado] || 'bg-gray-100 text-gray-800';
        const textoVisible = traducciones[fila.estado] || fila.estado;

        return <span className={`px-2 py-1 border-round text-sm font-medium ${claseColor}`}>{textoVisible}</span>;
    };

    if (cargando) return <div className="p-4 text-center">Generando reporte...</div>;
    if (!datosReporte) return null;

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
            <div className="flex flex-column sm:flex-row sm:align-items-start justify-content-between gap-3 mb-4">
                <div className="flex align-items-start gap-3">
                    <Button icon="pi pi-arrow-left" rounded text severity="secondary" aria-label="Volver" onClick={() => navigate(-1)} className="mt-1" />
                    <div>
                        <h1 className="m-0 text-2xl font-bold text-color">Reporte: Orden de Mérito</h1>
                        <p className="m-0 mt-1 text-base text-color-secondary">
                            {datosReporte.nombreEmpresa} - {datosReporte.nombrePuesto}
                        </p>

                        <div className="flex align-items-center gap-2 mt-2 text-sm text-500">
                            <i className="pi pi-user" />
                            <span>Emitido por: <strong>{datosReporte.emitidoPor}</strong></span>
                            <i className="pi pi-circle-fill text-xs mx-1" style={{ fontSize: '0.4rem' }} />
                            <i className="pi pi-calendar" />
                            <span>{new Date(datosReporte.fechaEmision).toLocaleString('es-AR')}</span>
                        </div>
                    </div>
                </div>

                <Button
                    label={generandoPdf ? 'Preparando...' : 'Descargar PDF'}
                    icon={generandoPdf ? 'pi pi-spin pi-spinner' : 'pi pi-file-pdf'}
                    severity="secondary"
                    onClick={manejarGenerarPdf}
                    disabled={generandoPdf}
                    className="w-full sm:w-auto"
                />
            </div>

            {/* TABLA WEB: APROBADOS */}
            <div className="surface-card border-1 surface-border border-round overflow-hidden flex flex-column">
                <div className="p-3 border-bottom-1 surface-border">
                    <h2 className="m-0 text-lg font-bold text-green-400">
                        <i className="pi pi-check-circle mr-2"></i>Candidatos en Orden de Mérito
                    </h2>
                </div>
                <DataTable
                    value={datosReporte.candidatosAprobados}
                    emptyMessage="No hay candidatos aprobados."
                    size="small"
                    stripedRows
                    sortField="puntaje"
                    sortOrder={-1}
                >
                    <Column header="Candidato" body={plantillaCandidato} sortable sortField="apellido" />
                    <Column header="Documento" body={plantillaDocumento} />
                    <Column header="Nº Candidato" field="numeroCandidato" align="center" sortable />
                    <Column header="Puntaje" body={plantillaPuntaje} align="center" sortable sortField="puntaje" className="font-bold text-green-500" />
                    <Column header="Accesos" field="cantidadAccesos" align="center" sortable />
                </DataTable>
            </div>

            {/* TABLA WEB: RECHAZADOS / INCOMPLETOS */}
            <div className="surface-card border-1 surface-border border-round overflow-hidden flex flex-column mt-4">
                <div className="p-3 border-bottom-1 surface-border">
                    <h2 className="m-0 text-lg font-bold text-red-400">
                        <i className="pi pi-times-circle mr-2"></i>Candidatos Fuera de Orden o Incompletos
                    </h2>
                </div>
                <DataTable
                    value={datosReporte.candidatosRechazadosOIncompletos}
                    emptyMessage="No hay candidatos en esta sección."
                    size="small"
                    stripedRows
                    sortField="puntaje"
                    sortOrder={-1}
                >
                    <Column header="Candidato" body={plantillaCandidato} sortable sortField="apellido" />
                    <Column header="Documento" body={plantillaDocumento} />
                    <Column header="Estado" body={plantillaEstado} align="center" sortable sortField="estado" />
                    <Column header="Puntaje" body={plantillaPuntaje} align="center" sortable sortField="puntaje" />
                </DataTable>
            </div>
        </div>
    );
};

export const MeritOrderReport = ReporteOrdenMerito;
export default ReporteOrdenMerito;
