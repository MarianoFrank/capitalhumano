import { useState, useEffect, useRef, useCallback, useDeferredValue } from 'react';
import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Sidebar } from 'primereact/sidebar';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { Tag } from 'primereact/tag';
import { Divider } from 'primereact/divider';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { ProgressBar } from 'primereact/progressbar';
import { apiCapitalHumano } from '../../config/api';
import { useToast } from '../../context/ContextoApp';
import { useTablasPaginadas } from '../../hooks/useTablasPaginadas';
import * as XLSX from 'xlsx';

export const GenerarEvaluacion = () => {
    // --- Contextos y Referencias ---
    const { mostrarExito, mostrarError } = useToast();
    const inputArchivoRef = useRef(null);

    // --- Estados de Datos y Carga ---
    const [candidatos, setCandidatos] = useState([]);
    const [listaPuestos, setListaPuestos] = useState([]);
    const [cargando, setCargando] = useState(false);
    const [generando, setGenerando] = useState(false);
    const [candidatosSeleccionados, setCandidatosSeleccionados] = useState([]);
    const [clavesGeneradas, setClavesGeneradas] = useState([]);

    // --- Hook de Paginación y Ordenamiento ---
    const estadoTabla = useTablasPaginadas({ campoOrdenPorDefecto: 'numeroCandidato' });

    // --- Estados de Filtros Principales ---
    const [filtros, setFiltros] = useState({
        nombre: '',
        apellido: '',
        numeroCandidato: ''
    });

    // --- Estados de Modales y Paneles ---
    const [panelVisible, setPanelVisible] = useState(false);
    const [modalPuestoVisible, setModalPuestoVisible] = useState(false);
    const [modalConfirmacionVisible, setModalConfirmacionVisible] = useState(false);
    const [modalExcelVisible, setModalExcelVisible] = useState(false);

    const [puestoSeleccionado, setPuestoSeleccionado] = useState(null);

    // --- Buscador en el Panel Lateral ---
    const [busquedaPanel, setBusquedaPanel] = useState('');
    const busquedaDiferida = useDeferredValue(busquedaPanel);

    // ==========================================
    // VALIDACIÓN DE COMPETENCIAS
    // ==========================================
    const competenciasInvalidas = puestoSeleccionado?.competencias?.filter(c => c.cumpleCondicion === false) || [];
    const deshabilitarSiguiente = !puestoSeleccionado || competenciasInvalidas.length > 0;

    // ==========================================
    // CARGA DE DATOS (API)
    // ==========================================
    const cargarCandidatos = useCallback(async () => {
        setCargando(true);
        const direccionOrden = estadoTabla.parametrosPaginacion.sortOrder === 1 ? 'asc' : 'desc';

        try {
            const params = {
                page: estadoTabla.parametrosPaginacion.page,
                size: estadoTabla.parametrosPaginacion.rows,
                sort: `${estadoTabla.parametrosPaginacion.sortField},${direccionOrden}`,
                ...(filtros.nombre && { nombre: filtros.nombre }),
                ...(filtros.apellido && { apellido: filtros.apellido }),
                ...(filtros.numeroCandidato && { numeroCandidato: filtros.numeroCandidato })
            };

            const respuesta = await apiCapitalHumano.get('/api/candidatos', { params });
            setCandidatos(respuesta.data.content);
            estadoTabla.setTotalRegistros(respuesta.data.totalElements);
        } catch {
            mostrarError('No se pudieron cargar los candidatos.');
        } finally {
            setCargando(false);
        }
    }, [estadoTabla.parametrosPaginacion, filtros.nombre, filtros.apellido, filtros.numeroCandidato, mostrarError]);

    const cargarPuestos = useCallback(async () => {
        try {
            const respuesta = await apiCapitalHumano.get('/api/puestos/select');
            setListaPuestos(respuesta.data);
        } catch {
            mostrarError('Hubo un error al cargar los puestos.');
        }
    }, [mostrarError]);

    useEffect(() => {
        cargarCandidatos();
        cargarPuestos();
    }, [cargarCandidatos, cargarPuestos]);

    // ==========================================
    // MANEJADORES DE ACCIÓN Y FILTROS
    // ==========================================
    const manejarBuscar = () => {
        estadoTabla.reiniciarPaginacion();
    };

    const manejarLimpiarFiltros = () => {
        setFiltros({ nombre: '', apellido: '', numeroCandidato: '' });
        manejarBuscar();
    };

    const quitarCandidato = useCallback((idCandidato) => {
        setCandidatosSeleccionados(prev => prev.filter(c => c.id !== idCandidato));
    }, []);

    const abrirSelectorArchivo = () => inputArchivoRef.current?.click();

    const manejarCargaArchivo = async (evento) => {
        const archivo = evento.target.files[0];
        if (!archivo) return;

        const formData = new FormData();
        formData.append('archivo', archivo);

        setCargando(true);
        try {
            const respuesta = await apiCapitalHumano.post('/api/candidatos/cargar-csv', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            mostrarExito(`Se importaron/actualizaron ${respuesta.data.length} candidatos.`);

            setCandidatosSeleccionados(prev => {
                const nuevos = respuesta.data.filter(
                    importado => !prev.some(seleccionado => seleccionado.id === importado.id)
                );
                return [...prev, ...nuevos];
            });

            cargarCandidatos();
        } catch {
            mostrarError('Hubo un error al procesar el archivo CSV.');
        } finally {
            setCargando(false);
            evento.target.value = null;
        }
    };

    // ==========================================
    // GENERACIÓN Y DESCARGA DE CLAVES
    // ==========================================
    const manejarGenerarEvaluacion = async () => {
        setGenerando(true);
        try {
            const payload = {
                idPuesto: puestoSeleccionado.id,
                idsCandidatos: candidatosSeleccionados.map(c => c.id)
            };

            const respuesta = await apiCapitalHumano.post('/api/evaluaciones/generar', payload);

            setClavesGeneradas(respuesta.data);
            setModalConfirmacionVisible(false);
            setModalExcelVisible(true);
        } catch (error) {
            mostrarError(error.response?.data?.message || 'Hubo un error al generar la evaluación.');
        } finally {
            setGenerando(false);
        }
    };

    const descargarExcel = () => {
        if (!clavesGeneradas || clavesGeneradas.length === 0) return;

        const datosExcel = clavesGeneradas.map(k => ({
            'Nro Candidato': k.numeroCandidato,
            'Nombre': k.nombre,
            'Apellido': k.apellido,
            'Clave de Acceso': k.claveAcceso
        }));

        const hoja = XLSX.utils.json_to_sheet(datosExcel);
        const libro = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(libro, hoja, 'Claves de Acceso');

        hoja['!cols'] = [
            { wch: 15 },
            { wch: 25 },
            { wch: 25 },
            { wch: 20 }
        ];

        const nombreSeguro = (puestoSeleccionado?.nombre || '').replace(/[^a-z0-9]/gi, '_');
        const nombreArchivo = `Claves_${nombreSeguro}.xlsx`;

        XLSX.writeFile(libro, nombreArchivo);

        setModalExcelVisible(false);
        mostrarExito('Evaluación generada y archivo descargado.');
        reiniciarFormulario();
    };

    const omitirExcel = () => {
        setModalExcelVisible(false);
        mostrarExito('Evaluación generada con éxito.');
        reiniciarFormulario();
    };

    const reiniciarFormulario = () => {
        setCandidatosSeleccionados([]);
        setPuestoSeleccionado(null);
        setClavesGeneradas([]);
    };

    // ==========================================
    // PLANTILLAS DE RENDERIZADO
    // ==========================================
    const plantillaAccionPanel = (fila) => (
        <Button icon="pi pi-times" severity="danger" text rounded aria-label="Quitar" onClick={() => quitarCandidato(fila.id)} />
    );

    const plantillaCandidatoPanel = (fila) => (
        <div className="flex flex-column gap-1">
            <span className="font-semibold text-sm">{fila.nombre} {fila.apellido}</span>
            <span className="text-xs text-color-secondary">Nro: {fila.numeroCandidato}</span>
        </div>
    );

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
            {/* --- ENCABEZADO --- */}
            <div className="flex flex-column sm:flex-row align-items-start sm:align-items-center justify-content-between gap-3">
                <div className="flex align-items-center gap-3">
                    <i className="pi pi-users text-3xl text-primary"></i>
                    <div>
                        <h1 className="m-0 text-2xl font-bold text-color">Evaluar Candidatos</h1>
                        <p className="m-0 mt-1 text-sm text-color-secondary">Seleccioná los candidatos que formarán parte de la evaluación</p>
                    </div>
                </div>
                <div>
                    <input type="file" accept=".csv" ref={inputArchivoRef} onChange={manejarCargaArchivo} style={{ display: 'none' }} />
                    <Button
                        label={cargando ? 'Procesando CSV...' : 'Importar Candidatos'}
                        icon={cargando ? 'pi pi-spin pi-spinner' : 'pi pi-upload'}
                        onClick={abrirSelectorArchivo}
                        disabled={cargando}
                    />
                </div>
            </div>

            {cargando && <ProgressBar mode="indeterminate" style={{ height: '4px' }} className="w-full mt-2" />}

            {/* --- TABLA Y FILTROS --- */}
            <div className="surface-card border-1 surface-border border-round overflow-hidden flex flex-column">
                <div className="p-4 border-bottom-1 surface-border flex flex-column md:flex-row gap-3 align-items-end">
                    <div className="flex flex-column flex-1 gap-2">
                        <label htmlFor="nombre" className="font-medium text-sm text-color-secondary">Nombre</label>
                        <InputText
                            id="nombre"
                            placeholder="Ingrese nombre..."
                            value={filtros.nombre}
                            onChange={(e) => setFiltros({ ...filtros, nombre: e.target.value })}
                            onKeyDown={(e) => e.key === 'Enter' && manejarBuscar()}
                        />
                    </div>

                    <div className="flex flex-column flex-1 gap-2">
                        <label htmlFor="apellido" className="font-medium text-sm text-color-secondary">Apellido</label>
                        <InputText
                            id="apellido"
                            placeholder="Ingrese apellido..."
                            value={filtros.apellido}
                            onChange={(e) => setFiltros({ ...filtros, apellido: e.target.value })}
                            onKeyDown={(e) => e.key === 'Enter' && manejarBuscar()}
                        />
                    </div>

                    <div className="flex flex-column flex-1 gap-2">
                        <label htmlFor="numeroCandidato" className="font-medium text-sm text-color-secondary">Número Candidato</label>
                        <InputText
                            type="number"
                            id="numeroCandidato"
                            placeholder="Ingrese número..."
                            value={filtros.numeroCandidato}
                            onChange={(e) => setFiltros({ ...filtros, numeroCandidato: e.target.value })}
                            onKeyDown={(e) => e.key === 'Enter' && manejarBuscar()}
                        />
                    </div>

                    <Button label="Buscar" icon="pi pi-search" onClick={manejarBuscar} className="w-full md:w-auto" />
                    <Button label="Limpiar" icon="pi pi-filter-slash" severity="secondary" outlined onClick={manejarLimpiarFiltros} />
                </div>

                <DataTable
                    value={candidatos}
                    dataKey="id"
                    selection={candidatosSeleccionados}
                    onSelectionChange={(e) => setCandidatosSeleccionados(e.value)}
                    lazy
                    paginator
                    first={estadoTabla.parametrosPaginacion.first}
                    rows={estadoTabla.parametrosPaginacion.rows}
                    totalRecords={estadoTabla.totalRegistros}
                    onPage={estadoTabla.alCambiarPagina}
                    onSort={estadoTabla.alCambiarOrden}
                    loading={cargando}
                    sortField={estadoTabla.parametrosPaginacion.sortField}
                    sortOrder={estadoTabla.parametrosPaginacion.sortOrder}
                    emptyMessage="No se encontraron candidatos."
                    size="small"
                    rowHover
                    stripedRows
                >
                    <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
                    <Column field="numeroCandidato" header="Nro. de Candidato" sortable sortField="numeroCandidato" className="text-color-secondary" style={{ width: '180px' }} />
                    <Column field="nombre" header="Nombre" sortable sortField="nombre" className="font-medium" />
                    <Column field="apellido" header="Apellido" sortable sortField="apellido" className="font-medium" />
                </DataTable>
            </div>

            {/* --- BARRA FLOTANTE INFERIOR --- */}
            {candidatosSeleccionados.length > 0 && (
                <div className="fixed bottom-0 left-0 right-0 glass-card py-3 px-4 shadow-3 z-5 flex flex-column sm:flex-row align-items-center justify-content-between gap-3 border-top-1 surface-border fadein">
                    <div className="flex align-items-center gap-3">
                        <Tag severity="success" value={candidatosSeleccionados.length} rounded className="px-3 py-2" icon="pi pi-check" />
                        <span className="font-medium text-color-secondary">candidatos seleccionados</span>
                    </div>

                    <div className="flex align-items-center gap-2 w-full sm:w-auto justify-content-end">
                        <Button label="Ver lista" icon="pi pi-list" severity="secondary" text onClick={() => setPanelVisible(true)} />
                        <Button label="Limpiar" icon="pi pi-trash" severity="danger" text onClick={() => setCandidatosSeleccionados([])} />
                        <Divider layout="vertical" className="mx-2" />
                        <Button label="Siguiente: Puesto" icon="pi pi-arrow-right" iconPos="right" onClick={() => setModalPuestoVisible(true)} />
                    </div>
                </div>
            )}

            {/* --- PANEL LATERAL DE SELECCIONADOS --- */}
            <Sidebar
                visible={panelVisible}
                position="right"
                onHide={() => setPanelVisible(false)}
                className="w-full md:w-30rem"
                header={<div className="font-bold text-xl">Candidatos Seleccionados</div>}
            >
                <div className="flex flex-column h-full pb-3">
                    <p className="text-color-secondary mb-3 text-sm flex-none">
                        Revisá la lista de postulantes elegidos antes de continuar.
                    </p>

                    <div className="mb-3 flex-none">
                        <IconField iconPosition="left" className="w-full">
                            <InputIcon className="pi pi-search" />
                            <InputText
                                value={busquedaPanel}
                                onChange={(e) => setBusquedaPanel(e.target.value)}
                                placeholder="Buscar por nombre, apellido o número..."
                                className="w-full"
                            />
                        </IconField>
                    </div>

                    <div className="border-1 surface-border border-round overflow-hidden">
                        <DataTable
                            value={candidatosSeleccionados.filter(c => {
                                const query = busquedaDiferida.toLowerCase();
                                const nombreCompleto = `${c.nombre} ${c.apellido}`.toLowerCase();
                                const numeroStr = String(c.numeroCandidato || '').toLowerCase();
                                return nombreCompleto.includes(query) || numeroStr.includes(query);
                            })}
                            dataKey="id"
                            emptyMessage={<div className="p-4 text-center text-color-secondary text-sm">No se encontraron candidatos.</div>}
                            size="small"
                            rowHover
                            className="p-datatable-sm"
                            scrollable
                            scrollHeight="calc(100vh - 290px)"
                            showHeaders={false}
                            pt={{ wrapper: { className: 'border-none' } }}
                        >
                            <Column body={plantillaCandidatoPanel} header="Postulante" />
                            <Column body={plantillaAccionPanel} headerStyle={{ width: '4rem' }} bodyStyle={{ textAlign: 'center' }} />
                        </DataTable>
                    </div>

                    <div className="mt-auto pt-3 surface-border flex-none">
                        <Button label="Cerrar panel" severity="secondary" text className="w-full" onClick={() => setPanelVisible(false)} />
                    </div>
                </div>
            </Sidebar>

            {/* --- MODAL: SELECCIÓN DE PUESTO --- */}
            <Dialog
                header="Seleccionar Puesto"
                visible={modalPuestoVisible}
                breakpoints={{ '960px': '75vw', '640px': '95vw' }}
                style={{ width: '45vw' }}
                onHide={() => setModalPuestoVisible(false)}
                footer={
                    <div className="flex justify-content-end gap-2 pt-4 border-top-1 surface-border mt-3">
                        <Button label="Cancelar" severity="secondary" text onClick={() => setModalPuestoVisible(false)} />
                        <Button
                            label="Siguiente"
                            icon="pi pi-arrow-right"
                            iconPos="right"
                            onClick={() => { setModalPuestoVisible(false); setModalConfirmacionVisible(true); }}
                            disabled={deshabilitarSiguiente}
                        />
                    </div>
                }
            >
                <div className="flex flex-column gap-4 pt-2">
                    <div className="flex flex-column gap-2">
                        <label className="font-semibold text-sm">Puesto a evaluar</label>
                        <Dropdown
                            filter
                            filterPlaceholder="Buscar puesto..."
                            value={puestoSeleccionado}
                            options={listaPuestos}
                            onChange={(e) => setPuestoSeleccionado(e.value)}
                            optionLabel="nombre"
                            placeholder="Seleccioná un puesto..."
                            className="w-full"
                        />
                    </div>

                    {puestoSeleccionado && (
                        <div className="p-4 border-round border-1 surface-border">
                            <span className="font-bold text-sm block mb-2">
                                Empresa: <span className="font-normal text-color-secondary">{puestoSeleccionado.empresa}</span>
                            </span>
                            <span className="font-bold text-sm block mb-3">Competencias requeridas:</span>
                            <div className="flex flex-wrap gap-2">
                                {puestoSeleccionado.competencias?.map((comp, idx) => (
                                    <Tag
                                        key={idx}
                                        severity={comp.cumpleCondicion === false ? 'danger' : 'secondary'}
                                        value={`${comp.nombre} (${comp.ponderacionRequerida})`}
                                    />
                                ))}
                            </div>

                            {competenciasInvalidas.length > 0 && (
                                <div className="mt-4 p-3 surface-ground border-round flex flex-column gap-2 border-left-3 border-pink-500 fadein">
                                    <span className="text-pink-500 font-semibold text-sm">
                                        <i className="pi pi-exclamation-triangle mr-2"></i>
                                        Atención: Las siguientes competencias no cumplen las condiciones para ser evaluadas:
                                    </span>
                                    <ul className="m-0 pl-4 text-sm text-color-secondary line-height-3">
                                        {competenciasInvalidas.map((comp, idx) => (
                                            <li key={idx}>{comp.nombre}</li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </Dialog>

            {/* --- MODAL: CONFIRMAR GENERACIÓN --- */}
            <Dialog
                header="Confirmar Generación"
                visible={modalConfirmacionVisible}
                breakpoints={{ '960px': '75vw', '640px': '95vw' }}
                style={{ width: '50vw' }}
                closable={!generando}
                onHide={() => setModalConfirmacionVisible(false)}
                footer={
                    <div className="flex justify-content-end gap-2 pt-4 border-top-1 surface-border mt-3">
                        <Button label="Atrás" severity="secondary" text disabled={generando} onClick={() => { setModalConfirmacionVisible(false); setModalPuestoVisible(true); }} />
                        <Button
                            label="Finalizar y Generar"
                            icon="pi pi-check"
                            severity="success"
                            loading={generando}
                            onClick={manejarGenerarEvaluacion}
                        />
                    </div>
                }
            >
                <p className="m-0 mb-4 text-color-secondary">
                    Estás por generar la evaluación del puesto <strong>{puestoSeleccionado?.nombre}</strong> para {candidatosSeleccionados.length} candidatos.
                </p>

                <div className="border-1 surface-border border-round overflow-hidden">
                    <DataTable
                        value={candidatosSeleccionados}
                        dataKey="id"
                        className="p-datatable-sm"
                        rowHover
                        scrollable
                        scrollHeight="350px"
                        pt={{ wrapper: { className: 'border-none' } }}
                    >
                        <Column field="nombre" header="Nombre" className="font-medium" />
                        <Column field="apellido" header="Apellido" className="font-medium" />
                        <Column field="numeroCandidato" header="Número Candidato" className="text-color-secondary" />
                    </DataTable>
                </div>
            </Dialog>

            {/* --- MODAL: DESCARGA EXCEL --- */}
            <Dialog
                visible={modalExcelVisible}
                breakpoints={{ '960px': '75vw', '640px': '95vw' }}
                style={{ width: '30vw' }}
                onHide={() => setModalExcelVisible(false)}
                showHeader={false}
                closable={false}
            >
                <div className="flex flex-column align-items-center gap-3 pt-5 pb-2 text-center">
                    <div className="flex align-items-center justify-content-center surface-100 border-circle w-4rem h-4rem mb-2">
                        <i className="pi pi-file-excel text-green-500 text-4xl"></i>
                    </div>
                    <h2 className="m-0 text-xl font-bold text-color">Evaluación Lista</h2>
                    <p className="m-0 text-color-secondary line-height-3 px-3">
                        ¿Deseás descargar la lista de candidatos con sus claves de acceso en un archivo Excel?
                    </p>
                    <div className="flex gap-3 mt-4 w-full">
                        <Button label="No, gracias" severity="secondary" outlined className="flex-1" onClick={omitirExcel} />
                        <Button label="Descargar Excel" severity="success" className="flex-1" onClick={descargarExcel} />
                    </div>
                </div>
            </Dialog>
        </div>
    );
};

export const GenerateEvaluation = GenerarEvaluacion;
export default GenerarEvaluacion;
