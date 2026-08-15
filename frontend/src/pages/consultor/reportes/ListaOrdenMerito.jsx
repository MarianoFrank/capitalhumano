import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { useMountEffect } from 'primereact/hooks';
import { apiCapitalHumano } from '../../../config/api';
import { useToast } from '../../../context/ContextoApp';
import { useTablasPaginadas } from '../../../hooks/useTablasPaginadas';

export const ListaOrdenMerito = () => {
    const { mostrarError } = useToast();
    const navigate = useNavigate();

    // --- Estados Principales ---
    const [puestos, setPuestos] = useState([]);
    const [cargando, setCargando] = useState(false);
    const [listaEmpresas, setListaEmpresas] = useState([]);

    // --- Estados del Modal ---
    const [modalVisible, setModalVisible] = useState(false);
    const [puestoSeleccionado, setPuestoSeleccionado] = useState(null);
    const [evaluaciones, setEvaluaciones] = useState([]);
    const [evaluacionSeleccionada, setEvaluacionSeleccionada] = useState(null);
    const [cargandoEvaluaciones, setCargandoEvaluaciones] = useState(false);

    // --- Paginación ---
    const estadoTabla = useTablasPaginadas({ campoOrdenPorDefecto: 'nombre' });

    // --- Filtros ---
    const [filtros, setFiltros] = useState({
        idEmpresa: null,
        nombrePuesto: '',
        codigo: ''
    });

    // --- Carga Inicial ---
    useMountEffect(() => {
        apiCapitalHumano.get('/api/empresas/select')
            .then(res => setListaEmpresas(res.data))
            .catch(() => mostrarError('No se pudieron cargar las empresas.'));
    });

    // --- Fetch de la Tabla ---
    const cargarDatos = useCallback(async () => {
        setCargando(true);
        const params = {
            page: estadoTabla.parametrosPaginacion.page,
            size: estadoTabla.parametrosPaginacion.rows,
            ...(filtros.idEmpresa && { idEmpresa: filtros.idEmpresa }),
            ...(filtros.nombrePuesto && { nombrePuesto: filtros.nombrePuesto }),
            ...(filtros.codigo && { codigo: filtros.codigo })
        };

        try {
            const respuesta = await apiCapitalHumano.get('/api/reportes/puestos', { params });
            setPuestos(respuesta.data.content);
            estadoTabla.setTotalRegistros(respuesta.data.totalElements);
        } catch {
            mostrarError('Hubo un error al obtener los puestos.');
        } finally {
            setCargando(false);
        }
    }, [estadoTabla.parametrosPaginacion, filtros.idEmpresa, filtros.nombrePuesto, filtros.codigo, mostrarError]);

    useEffect(() => {
        cargarDatos();
    }, [cargarDatos]);

    const manejarBuscar = () => estadoTabla.reiniciarPaginacion();

    // --- Lógica del Modal ---
    const abrirModal = async (puesto) => {
        setPuestoSeleccionado(puesto);
        setEvaluacionSeleccionada(null);
        setModalVisible(true);
        setCargandoEvaluaciones(true);

        try {
            const respuesta = await apiCapitalHumano.get(`/api/reportes/puestos/${puesto.id}/evaluaciones`);
            setEvaluaciones(respuesta.data);
        } catch {
            mostrarError('Error al cargar las evaluaciones del puesto.');
        } finally {
            setCargandoEvaluaciones(false);
        }
    };

    const manejarGenerar = () => {
        if (evaluacionSeleccionada) {
            navigate(`/reportes/orden-merito/${puestoSeleccionado.id}?idEvaluacion=${evaluacionSeleccionada}`);
        } else {
            navigate(`/reportes/orden-merito/${puestoSeleccionado.id}`);
        }
    };

    const manejarLimpiarFiltros = () => {
        setFiltros({ idEmpresa: null, nombrePuesto: '', codigo: '' });
        estadoTabla.reiniciarPaginacion();
    };

    // --- Plantillas ---
    const plantillaAccion = (fila) => (
        <Button
            label="Emitir orden de mérito"
            size="small"
            outlined
            severity="secondary"
            onClick={() => abrirModal(fila)}
        />
    );

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
            <div className="flex align-items-center gap-3 mb-2">
                <div>
                    <h1 className="m-0 text-2xl font-bold text-color">Orden de Mérito</h1>
                </div>
            </div>

            <div className="surface-card border-1 surface-border border-round overflow-hidden flex flex-column">
                {/* Filtros Integrados */}
                <div className="p-4 border-bottom-1 surface-border flex flex-column md:flex-row gap-3 align-items-end">
                    <div className="flex flex-column flex-1 gap-2">
                        <label className="font-medium text-sm text-color-secondary">Empresa</label>
                        <Dropdown
                            filter
                            value={filtros.idEmpresa}
                            options={listaEmpresas}
                            onChange={(e) => setFiltros({ ...filtros, idEmpresa: e.value })}
                            optionLabel="nombre"
                            optionValue="id"
                            placeholder="Seleccione una empresa"
                            className="w-full"
                        />
                    </div>
                    <div className="flex flex-column flex-1 gap-2">
                        <label className="font-medium text-sm text-color-secondary">Puesto</label>
                        <InputText
                            value={filtros.nombrePuesto}
                            onChange={(e) => setFiltros({ ...filtros, nombrePuesto: e.target.value })}
                            placeholder="Seleccione un puesto"
                            className="w-full"
                        />
                    </div>
                    <div className="flex flex-column flex-1 gap-2">
                        <label className="font-medium text-sm text-color-secondary">Código</label>
                        <InputText
                            value={filtros.codigo}
                            onChange={(e) => setFiltros({ ...filtros, codigo: e.target.value })}
                            onKeyDown={(e) => e.key === 'Enter' && manejarBuscar()}
                            placeholder="Ingrese el código del puesto"
                            className="w-full"
                        />
                    </div>
                    <Button label="Buscar" icon="pi pi-search" onClick={manejarBuscar} className="w-full md:w-auto" />
                    <Button
                        label="Limpiar"
                        icon="pi pi-filter-slash"
                        severity="secondary"
                        outlined
                        onClick={manejarLimpiarFiltros}
                    />
                </div>

                {/* Tabla de Puestos */}
                <DataTable
                    value={puestos}
                    lazy
                    paginator
                    first={estadoTabla.parametrosPaginacion.first}
                    rows={estadoTabla.parametrosPaginacion.rows}
                    totalRecords={estadoTabla.totalRegistros}
                    onPage={estadoTabla.alCambiarPagina}
                    loading={cargando}
                    emptyMessage="No se encontraron puestos."
                    size="small"
                    rowHover
                    stripedRows
                >
                    <Column field="codigo" header="Código" />
                    <Column field="nombrePuesto" header="Nombre del puesto" />
                    <Column field="nombreEmpresa" header="Empresa" />
                    <Column field="totalCandidatos" header="Candidatos" align="center" />
                    <Column field="evaluacionesCompletadas" header="Evaluaciones completadas" align="center" />
                    <Column header="Acción" body={plantillaAccion} align="center" />
                </DataTable>
            </div>

            {/* Modal de Selección de Evaluación */}
            <Dialog
                header={`Puesto ${puestoSeleccionado?.codigo || ''}`}
                visible={modalVisible}
                style={{ width: '450px' }}
                onHide={() => setModalVisible(false)}
            >
                <div className="flex flex-column gap-3 pt-2">
                    <label className="font-medium text-color-secondary">Seleccione una o todas las evaluaciones</label>
                    <Dropdown
                        value={evaluacionSeleccionada}
                        options={evaluaciones}
                        onChange={(e) => setEvaluacionSeleccionada(e.value)}
                        optionLabel="descripcion"
                        optionValue="id"
                        placeholder="Todas"
                        showClear
                        loading={cargandoEvaluaciones}
                        className="w-full"
                    />
                    <div className="flex justify-content-end mt-3">
                        <Button label="Generar" onClick={manejarGenerar} />
                    </div>
                </div>
            </Dialog>
        </div>
    );
};

export const MeritOrderList = ListaOrdenMerito;
export default ListaOrdenMerito;
