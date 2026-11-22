import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { useMountEffect } from 'primereact/hooks';
import { preguntasService } from '../../../services/preguntas';
import { useToast } from '../../../context';
import { usePaginacion } from '../../../hooks/usePaginacion';

export default function ListaPreguntas() {
    const { mostrarError } = useToast();
    const navigate = useNavigate();

    const [preguntas, setPreguntas] = useState([]);
    const [cargando, setCargando] = useState(false);
    const [listaCompetencias, setListaCompetencias] = useState([]);
    const [listaFactores, setListaFactores] = useState([]);

    const estadoTabla = usePaginacion({ campoOrdenPorDefecto: 'fechaModificacion', ordenPorDefecto: -1 });

    const [filtros, setFiltros] = useState({
        idCompetencia: null,
        idFactor: null,
        nombrePregunta: ''
    });

    useMountEffect(() => {
        preguntasService.obtenerCompetenciasSelect()
            .then((data) => setListaCompetencias(data))
            .catch(() => mostrarError('No se pudieron cargar las competencias.'));
    });

    const cargarDatos = useCallback(async () => {
        setCargando(true);
        const direccionOrden = estadoTabla.parametrosPaginacion.sortOrder === 1 ? 'asc' : 'desc';

        try {
            const data = await preguntasService.obtenerPaginadas({
                page: estadoTabla.parametrosPaginacion.page,
                size: estadoTabla.parametrosPaginacion.rows,
                sort: `${estadoTabla.parametrosPaginacion.sortField},${direccionOrden}`,
                ...filtros
            });
            setPreguntas(data.content);
            estadoTabla.setTotalRegistros(data.totalElements);
        } catch {
            mostrarError('Hubo un error al obtener las preguntas.');
        } finally {
            setCargando(false);
        }
    }, [estadoTabla.parametrosPaginacion, filtros, mostrarError]);

    useEffect(() => {
        cargarDatos();
    }, [cargarDatos]);

    const manejarCambioCompetencia = (e) => {
        const idSeleccionado = e.value;
        setFiltros((prev) => ({ ...prev, idCompetencia: idSeleccionado, idFactor: null }));
        setListaFactores([]);

        if (idSeleccionado) {
            preguntasService.obtenerFactoresSelect(idSeleccionado)
                .then((data) => setListaFactores(data))
                .catch(() => mostrarError('No se pudieron cargar los factores.'));
        }
    };

    const manejarBuscar = () => estadoTabla.reiniciarPaginacion();

    const manejarLimpiarFiltros = () => {
        setFiltros({ idCompetencia: null, idFactor: null, nombrePregunta: '' });
        estadoTabla.reiniciarPaginacion();
    };

    const plantillaAcciones = () => (
        <div className="flex justify-content-center gap-2">
            <Button size="small" icon="pi pi-pencil" severity="secondary" text rounded tooltip="Modificar" tooltipOptions={{ position: 'top' }} />
            <Button size="small" icon="pi pi-trash" severity="danger" text rounded tooltip="Eliminar" tooltipOptions={{ position: 'top' }} />
        </div>
    );

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
            <div className="flex flex-column sm:flex-row align-items-start sm:align-items-center justify-content-between gap-3 mb-2">
                <div className="flex align-items-center gap-3">
                    <i className="pi pi-list text-3xl text-primary"></i>
                    <div>
                        <h1 className="m-0 text-2xl font-bold text-color">Banco de Preguntas</h1>
                        <p className="m-0 mt-1 text-sm text-color-secondary">Gestioná las preguntas para las evaluaciones</p>
                    </div>
                </div>
                <Button label="Crear Pregunta" icon="pi pi-plus" onClick={() => navigate('/preguntas/nueva')} />
            </div>

            <div className="surface-card border-1 surface-border border-round overflow-hidden flex flex-column">
                <div className="p-4 border-bottom-1 surface-border flex flex-column md:flex-row gap-3 align-items-end">
                    <div className="flex flex-column flex-1 gap-2">
                        <label className="font-medium text-sm text-color-secondary">Competencia</label>
                        <Dropdown
                            value={filtros.idCompetencia}
                            filter
                            options={listaCompetencias}
                            onChange={manejarCambioCompetencia}
                            optionLabel="nombre"
                            optionValue="id"
                            placeholder="Seleccione competencia"
                            className="w-full"
                        />
                    </div>
                    <div className="flex flex-column flex-1 gap-2">
                        <label className="font-medium text-sm text-color-secondary">Factor</label>
                        <Dropdown
                            value={filtros.idFactor}
                            options={listaFactores}
                            onChange={(e) => setFiltros((prev) => ({ ...prev, idFactor: e.value }))}
                            optionLabel="nombre"
                            optionValue="id"
                            placeholder="Seleccione un factor"
                            disabled={!filtros.idCompetencia}
                            className="w-full"
                        />
                    </div>
                    <div className="flex flex-column flex-1 gap-2">
                        <label className="font-medium text-sm text-color-secondary">Nombre de pregunta</label>
                        <InputText
                            value={filtros.nombrePregunta}
                            onChange={(e) => setFiltros((prev) => ({ ...prev, nombrePregunta: e.target.value }))}
                            onKeyDown={(e) => e.key === 'Enter' && manejarBuscar()}
                            placeholder="Ej. Nivel de adaptabilidad..."
                            className="w-full"
                        />
                    </div>
                    <Button label="Buscar" icon="pi pi-search" onClick={manejarBuscar} className="w-full md:w-auto" />
                    <Button label="Limpiar" icon="pi pi-filter-slash" severity="secondary" outlined onClick={manejarLimpiarFiltros} />
                </div>

                <DataTable
                    value={preguntas}
                    lazy
                    paginator
                    first={estadoTabla.parametrosPaginacion.first}
                    rows={estadoTabla.parametrosPaginacion.rows}
                    totalRecords={estadoTabla.totalRegistros}
                    onPage={estadoTabla.alCambiarPagina}
                    onSort={estadoTabla.alCambiarOrden}
                    sortField={estadoTabla.parametrosPaginacion.sortField}
                    sortOrder={estadoTabla.parametrosPaginacion.sortOrder}
                    loading={cargando}
                    emptyMessage="No se encontraron preguntas con estos filtros."
                    size="small"
                    rowHover
                    stripedRows
                >
                    <Column field="nombreCompetencia" header="Competencia" sortable sortField="factor.competencia.nombre" />
                    <Column field="nombreFactor" header="Factor" sortable sortField="factor.nombre" />
                    <Column field="nombrePregunta" header="Pregunta" sortable sortField="nombre" className="font-semibold" />
                    <Column field="fechaModificacion" header="Última Modif." sortable sortField="fechaModificacion" className="text-color-secondary text-sm" />
                    <Column header="Acciones" body={plantillaAcciones} align="center" />
                </DataTable>
            </div>
        </div>
    );
}
