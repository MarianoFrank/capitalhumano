import { useState, useEffect, useRef, useCallback } from 'react';
import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ProgressBar } from 'primereact/progressbar';
import { useToast } from '../../../context';
import { usePaginacion } from '../../../hooks/usePaginacion';
import { candidatosService } from '../../../services/candidatos';
import { puestosService } from '../../../services/puestos';
import { evaluacionesService } from '../../../services/evaluaciones';
import { exportarClavesExcel } from '../../../utils';

// Subcomponentes modulares
import BarraSeleccionados from './BarraSeleccionados';
import PanelCandidatosSeleccionados from './PanelCandidatosSeleccionados';
import ModalSeleccionarPuesto from './ModalSeleccionarPuesto';
import ModalConfirmarEvaluacion from './ModalConfirmarEvaluacion';
import ModalDescargaExcel from './ModalDescargaExcel';

export default function GenerarEvaluacion() {
    const { mostrarExito, mostrarError } = useToast();
    const inputArchivoRef = useRef(null);

    const [candidatos, setCandidatos] = useState([]);
    const [listaPuestos, setListaPuestos] = useState([]);
    const [cargando, setCargando] = useState(false);
    const [generando, setGenerando] = useState(false);
    const [candidatosSeleccionados, setCandidatosSeleccionados] = useState([]);
    const [clavesGeneradas, setClavesGeneradas] = useState([]);

    const estadoTabla = usePaginacion({ campoOrdenPorDefecto: 'numeroCandidato' });

    const [filtros, setFiltros] = useState({
        nombre: '',
        apellido: '',
        numeroCandidato: ''
    });

    const [panelVisible, setPanelVisible] = useState(false);
    const [modalPuestoVisible, setModalPuestoVisible] = useState(false);
    const [modalConfirmacionVisible, setModalConfirmacionVisible] = useState(false);
    const [modalExcelVisible, setModalExcelVisible] = useState(false);
    const [puestoSeleccionado, setPuestoSeleccionado] = useState(null);

    const cargarCandidatos = useCallback(async () => {
        setCargando(true);
        const direccionOrden = estadoTabla.parametrosPaginacion.sortOrder === 1 ? 'asc' : 'desc';

        try {
            const data = await candidatosService.obtenerPaginados({
                page: estadoTabla.parametrosPaginacion.page,
                size: estadoTabla.parametrosPaginacion.rows,
                sort: `${estadoTabla.parametrosPaginacion.sortField},${direccionOrden}`,
                ...filtros
            });
            setCandidatos(data.content);
            estadoTabla.setTotalRegistros(data.totalElements);
        } catch {
            mostrarError('No se pudieron cargar los candidatos.');
        } finally {
            setCargando(false);
        }
    }, [estadoTabla.parametrosPaginacion, filtros, mostrarError]);

    const cargarPuestos = useCallback(async () => {
        try {
            const data = await puestosService.obtenerParaSelect();
            setListaPuestos(data);
        } catch {
            mostrarError('Hubo un error al cargar los puestos.');
        }
    }, [mostrarError]);

    useEffect(() => {
        cargarCandidatos();
        cargarPuestos();
    }, [cargarCandidatos, cargarPuestos]);

    const manejarBuscar = () => estadoTabla.reiniciarPaginacion();

    const manejarLimpiarFiltros = () => {
        setFiltros({ nombre: '', apellido: '', numeroCandidato: '' });
        estadoTabla.reiniciarPaginacion();
    };

    const quitarCandidato = useCallback((idCandidato) => {
        setCandidatosSeleccionados((prev) => prev.filter((c) => c.id !== idCandidato));
    }, []);

    const manejarCargaArchivo = async (evento) => {
        const archivo = evento.target.files[0];
        if (!archivo) return;

        setCargando(true);
        try {
            const data = await candidatosService.cargarCsv(archivo);
            mostrarExito(`Se importaron/actualizaron ${data.length} candidatos.`);

            setCandidatosSeleccionados((prev) => {
                const nuevos = data.filter((importado) => !prev.some((sel) => sel.id === importado.id));
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

    const manejarContinuarPuesto = async () => {
        try {
            await evaluacionesService.validarCandidatos(candidatosSeleccionados.map((c) => c.id));
            setModalPuestoVisible(true);
        } catch (error) {
            mostrarError(error.response?.data?.message || 'Algunos candidatos seleccionados ya poseen cuestionarios activos o en proceso.');
        }
    };

    const manejarGenerarEvaluacion = async () => {
        setGenerando(true);
        try {
            const data = await evaluacionesService.generar({
                idPuesto: puestoSeleccionado.id,
                idsCandidatos: candidatosSeleccionados.map((c) => c.id)
            });

            setClavesGeneradas(data);
            setModalConfirmacionVisible(false);
            setModalExcelVisible(true);
        } catch (error) {
            mostrarError(error.response?.data?.message || 'Hubo un error al generar la evaluación.');
        } finally {
            setGenerando(false);
        }
    };

    const reiniciarFormulario = () => {
        setCandidatosSeleccionados([]);
        setPuestoSeleccionado(null);
        setClavesGeneradas([]);
    };

    const manejarDescargarExcel = () => {
        exportarClavesExcel(clavesGeneradas, puestoSeleccionado?.nombre);
        setModalExcelVisible(false);
        mostrarExito('Evaluación generada y archivo descargado.');
        reiniciarFormulario();
    };

    const manejarOmitirExcel = () => {
        setModalExcelVisible(false);
        mostrarExito('Evaluación generada con éxito.');
        reiniciarFormulario();
    };

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
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
                        onClick={() => inputArchivoRef.current?.click()}
                        disabled={cargando}
                    />
                </div>
            </div>

            {cargando && <ProgressBar mode="indeterminate" style={{ height: '4px' }} className="w-full mt-2" />}

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

            <BarraSeleccionados
                cantidadSeleccionados={candidatosSeleccionados.length}
                alAbrirPanel={() => setPanelVisible(true)}
                alLimpiar={() => setCandidatosSeleccionados([])}
                alContinuarPuesto={manejarContinuarPuesto}
            />

            <PanelCandidatosSeleccionados
                visible={panelVisible}
                alCerrar={() => setPanelVisible(false)}
                candidatosSeleccionados={candidatosSeleccionados}
                alQuitarCandidato={quitarCandidato}
            />

            <ModalSeleccionarPuesto
                visible={modalPuestoVisible}
                alCerrar={() => setModalPuestoVisible(false)}
                alAvanzar={() => { setModalPuestoVisible(false); setModalConfirmacionVisible(true); }}
                puestoSeleccionado={puestoSeleccionado}
                alSeleccionarPuesto={setPuestoSeleccionado}
                listaPuestos={listaPuestos}
            />

            <ModalConfirmarEvaluacion
                visible={modalConfirmacionVisible}
                alCerrar={() => setModalConfirmacionVisible(false)}
                alAtras={() => { setModalConfirmacionVisible(false); setModalPuestoVisible(true); }}
                alConfirmar={manejarGenerarEvaluacion}
                puestoSeleccionado={puestoSeleccionado}
                candidatosSeleccionados={candidatosSeleccionados}
                generando={generando}
            />

            <ModalDescargaExcel
                visible={modalExcelVisible}
                alOmitir={manejarOmitirExcel}
                alDescargar={manejarDescargarExcel}
            />
        </div>
    );
}
