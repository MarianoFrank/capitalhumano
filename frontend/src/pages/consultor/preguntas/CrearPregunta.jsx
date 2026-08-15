import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { InputNumber } from 'primereact/inputnumber';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { useMountEffect } from 'primereact/hooks';
import { apiCapitalHumano } from '../../../config/api';
import { useToast } from '../../../context/ContextoApp';

export const CrearPregunta = () => {
    const { mostrarExito, mostrarError } = useToast();
    const navigate = useNavigate();

    const [datosFormulario, setDatosFormulario] = useState({
        idCompetencia: null,
        idFactor: null,
        nombre: '',
        descripcion: '',
        texto: '',
        tipo: 'SINGLE_CHOICE',
        opciones: []
    });

    const [textoNuevaOpcion, setTextoNuevaOpcion] = useState('');

    const [modalIaVisible, setModalIaVisible] = useState(false);
    const [contextoExtra, setContextoExtra] = useState('');
    const [generandoIa, setGenerandoIa] = useState(false);

    const [listaCompetencias, setListaCompetencias] = useState([]);
    const [listaFactores, setListaFactores] = useState([]);

    const tiposPregunta = [
        { label: 'Selección única (Single Choice)', value: 'SINGLE_CHOICE' },
        { label: 'Selección múltiple (Multiple Choice)', value: 'MULTIPLE_CHOICE' }
    ];

    useMountEffect(() => {
        apiCapitalHumano.get('/api/competencias/select')
            .then(res => setListaCompetencias(res.data))
            .catch(() => mostrarError('No se pudieron cargar las competencias.'));
    });

    const manejarCambioCompetencia = (e) => {
        const idSeleccionado = e.value;
        setDatosFormulario(prev => ({ ...prev, idCompetencia: idSeleccionado, idFactor: null }));
        setListaFactores([]);

        if (idSeleccionado) {
            apiCapitalHumano.get(`/api/factores/select?idCompetencia=${idSeleccionado}`)
                .then(res => setListaFactores(res.data))
                .catch(() => mostrarError('No se pudieron cargar los factores.'));
        }
    };

    // --- MANEJO DE OPCIONES ---
    const agregarOpcion = () => {
        if (!textoNuevaOpcion.trim()) return;
        setDatosFormulario(prev => ({
            ...prev,
            opciones: [...prev.opciones, { texto: textoNuevaOpcion, ponderacion: 0 }]
        }));
        setTextoNuevaOpcion('');
    };

    const quitarOpcion = (indiceAEliminar) => {
        setDatosFormulario(prev => ({
            ...prev,
            opciones: prev.opciones.filter((_, idx) => idx !== indiceAEliminar)
        }));
    };

    const manejarCambioPonderacion = (indice, nuevaPonderacion) => {
        setDatosFormulario(prev => {
            const opcionesActualizadas = [...prev.opciones];
            opcionesActualizadas[indice].ponderacion = nuevaPonderacion;
            return { ...prev, opciones: opcionesActualizadas };
        });
    };

    const manejarCambioTextoOpcion = (indice, nuevoTexto) => {
        setDatosFormulario(prev => {
            const opcionesActualizadas = [...prev.opciones];
            opcionesActualizadas[indice].texto = nuevoTexto;
            return { ...prev, opciones: opcionesActualizadas };
        });
    };

    // --- GUARDADO MANUAL ---
    const manejarGuardar = async () => {
        if (!datosFormulario.idFactor) return mostrarError('Debés seleccionar un factor.');
        if (!datosFormulario.nombre.trim()) return mostrarError('El nombre es obligatorio.');
        if (!datosFormulario.texto.trim()) return mostrarError('El texto de la pregunta es obligatorio.');
        if (datosFormulario.opciones.length < 2) return mostrarError('Debés agregar al menos 2 opciones de respuesta.');

        const payload = {
            idFactor: datosFormulario.idFactor,
            nombre: datosFormulario.nombre,
            descripcion: datosFormulario.descripcion,
            texto: datosFormulario.texto,
            tipo: datosFormulario.tipo,
            opciones: datosFormulario.opciones.map((opt, idx) => ({
                ordenVisualizacion: idx + 1,
                ponderacion: opt.ponderacion || 0,
                texto: opt.texto
            }))
        };

        try {
            await apiCapitalHumano.post('/api/preguntas', payload);
            mostrarExito('¡Pregunta creada con éxito!');
            setTimeout(() => navigate('/preguntas'), 1200);
        } catch {
            mostrarError('Hubo un error al guardar la pregunta.');
        }
    };

    // --- GENERACIÓN CON IA ---
    const manejarGenerarConIa = async () => {
        const comp = listaCompetencias.find(c => c.id === datosFormulario.idCompetencia);
        const fact = listaFactores.find(f => f.id === datosFormulario.idFactor);

        if (!comp || !fact) return mostrarError('Seleccioná una competencia y un factor primero.');

        setGenerandoIa(true);
        try {
            const payload = {
                nombreCompetencia: comp.nombre,
                nombreFactor: fact.nombre,
                nombrePregunta: datosFormulario.nombre,
                descripcion: datosFormulario.descripcion,
                contextoExtra: contextoExtra
            };

            const respuesta = await apiCapitalHumano.post('/api/ia/generar-pregunta', payload);

            setDatosFormulario(prev => ({
                ...prev,
                nombre: respuesta.data.nombrePregunta || prev.nombre,
                descripcion: respuesta.data.descripcion || prev.descripcion,
                tipo: respuesta.data.tipo || prev.tipo,
                texto: respuesta.data.texto,
                opciones: (respuesta.data.opciones || []).map(o => ({
                    texto: o.texto,
                    ponderacion: o.ponderacion
                }))
            }));

            mostrarExito('¡Pregunta generada con éxito!');
            setModalIaVisible(false);
            setContextoExtra('');
        } catch {
            mostrarError('Hubo un error al conectar con la Inteligencia Artificial.');
        } finally {
            setGenerandoIa(false);
        }
    };

    const pieModalIa = (
        <div className="flex justify-content-end gap-2 pt-3 border-top-1 surface-border mt-3">
            <Button label="Cancelar" icon="pi pi-times" severity="secondary" text onClick={() => setModalIaVisible(false)} disabled={generandoIa} />
            <Button label={generandoIa ? 'Generando...' : 'Generar IA'} icon="pi pi-sparkles" loading={generandoIa} onClick={manejarGenerarConIa} autoFocus />
        </div>
    );

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
            <div className="flex align-items-center gap-3 mb-2">
                <i className="pi pi-plus-circle text-3xl text-primary"></i>
                <div>
                    <h1 className="m-0 text-2xl font-bold text-color">Crear Pregunta</h1>
                    <p className="m-0 mt-1 text-sm text-color-secondary">Añadí un nuevo escenario al banco de evaluación</p>
                </div>
            </div>

            {/* Formulario Principal */}
            <div className="surface-card border-1 surface-border border-round p-5 flex flex-column gap-4">
                <div className="formgrid grid">
                    <div className="field col-12 md:col-6 flex flex-column gap-2">
                        <label className="font-semibold text-sm text-color-secondary">Competencia</label>
                        <Dropdown
                            value={datosFormulario.idCompetencia}
                            options={listaCompetencias}
                            onChange={manejarCambioCompetencia}
                            optionLabel="nombre"
                            optionValue="id"
                            placeholder="Seleccione una competencia"
                            className="w-full"
                        />
                    </div>
                    <div className="field col-12 md:col-6 flex flex-column gap-2">
                        <label className="font-semibold text-sm text-color-secondary">Factor</label>
                        <Dropdown
                            value={datosFormulario.idFactor}
                            options={listaFactores}
                            onChange={(e) => setDatosFormulario(prev => ({ ...prev, idFactor: e.value }))}
                            optionLabel="nombre"
                            optionValue="id"
                            placeholder="Seleccione un factor"
                            disabled={!datosFormulario.idCompetencia}
                            className="w-full"
                        />
                    </div>
                </div>

                <div className="flex flex-column gap-2">
                    <label className="font-semibold text-sm text-color-secondary">Nombre de la pregunta</label>
                    <InputText
                        value={datosFormulario.nombre}
                        onChange={(e) => setDatosFormulario(prev => ({ ...prev, nombre: e.target.value }))}
                        placeholder="Ej. Resolución de consultas SQL lentas"
                        className="w-full"
                    />
                </div>

                <div className="flex flex-column gap-2">
                    <label className="font-semibold text-sm text-color-secondary">Descripción (opcional)</label>
                    <InputTextarea
                        value={datosFormulario.descripcion}
                        onChange={(e) => setDatosFormulario(prev => ({ ...prev, descripcion: e.target.value }))}
                        placeholder="Describa el objetivo de la pregunta..."
                        rows={2}
                        className="w-full resize-none"
                        autoResize
                    />
                </div>

                <div className="flex flex-column gap-2">
                    <label className="font-semibold text-sm text-color-secondary">Texto de la Pregunta</label>
                    <div className="flex flex-column sm:flex-row gap-3 align-items-start">
                        <InputTextarea
                            value={datosFormulario.texto}
                            onChange={(e) => setDatosFormulario(prev => ({ ...prev, texto: e.target.value }))}
                            placeholder="Ingrese el enunciado de la pregunta detallado..."
                            className="w-full flex-1 resize-none"
                            autoResize
                            rows={2}
                        />
                        <Button
                            label="Asistir con IA"
                            icon="pi pi-sparkles"
                            severity="info"
                            outlined
                            className="white-space-nowrap"
                            onClick={() => {
                                (!datosFormulario.idCompetencia || !datosFormulario.idFactor)
                                    ? mostrarError('Elegí una competencia y un factor primero.')
                                    : setModalIaVisible(true);
                            }}
                        />
                    </div>
                </div>

                <div className="flex flex-column gap-2 mb-2">
                    <label className="font-semibold text-sm text-color-secondary">Tipo de Respuesta</label>
                    <Dropdown
                        value={datosFormulario.tipo}
                        options={tiposPregunta}
                        onChange={(e) => setDatosFormulario(prev => ({ ...prev, tipo: e.value }))}
                        placeholder="Seleccione un tipo"
                        className="w-full md:w-20rem"
                    />
                </div>

                {/* --- ZONA DE OPCIONES --- */}
                <div className="flex flex-column gap-3">
                    <label className="font-semibold text-sm text-color-secondary">Opciones de Respuesta (Arrastrá para ordenar)</label>

                    <div className="border-1 surface-border border-round overflow-hidden flex flex-column">
                        <DataTable
                            showHeaders={false}
                            className="shadow-none border-none p-datatable-sm"
                            value={datosFormulario.opciones}
                            reorderableRows
                            onRowReorder={(e) => setDatosFormulario(prev => ({ ...prev, opciones: e.value }))}
                            emptyMessage={<div className="p-4 text-center text-color-secondary">Todavía no agregaste ninguna opción.</div>}
                        >
                            <Column rowReorder style={{ width: '3rem', textAlign: 'center' }} />

                            <Column body={(fila, props) => <span className="font-medium text-color-secondary">{props.rowIndex + 1}.</span>} style={{ width: '3rem' }} />

                            <Column body={(fila, props) => (
                                <InputText
                                    value={fila.texto}
                                    onChange={(e) => manejarCambioTextoOpcion(props.rowIndex, e.target.value)}
                                    className="w-full"
                                    placeholder="Escribí la opción..."
                                />
                            )} />

                            <Column body={(fila, props) => (
                                <div className="flex align-items-center justify-content-end gap-2">
                                    <span className="text-xs text-color-secondary font-semibold uppercase tracking-wider">Ponderación</span>
                                    <InputNumber
                                        value={fila.ponderacion}
                                        onValueChange={(e) => manejarCambioPonderacion(props.rowIndex, e.value)}
                                        showButtons
                                        buttonLayout="horizontal"
                                        decrementButtonClassName="surface-border p-button-text p-button-plain z-0"
                                        incrementButtonClassName="surface-border p-button-text p-button-plain"
                                        incrementButtonIcon="pi pi-plus"
                                        decrementButtonIcon="pi pi-minus"
                                        inputClassName="w-3rem text-center"
                                        min={0}
                                        max={10}
                                    />
                                </div>
                            )} style={{ width: '16rem' }} />

                            <Column body={(fila, props) => (
                                <Button icon="pi pi-trash" severity="danger" text rounded aria-label="Eliminar" onClick={() => quitarOpcion(props.rowIndex)} />
                            )} style={{ width: '4rem', textAlign: 'center' }} />
                        </DataTable>

                        <div className="surface-ground p-3 surface-border flex flex-column sm:flex-row align-items-center gap-3">
                            <InputText
                                value={textoNuevaOpcion}
                                onChange={(e) => setTextoNuevaOpcion(e.target.value)}
                                onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); agregarOpcion(); } }}
                                placeholder={`Escribí la opción ${datosFormulario.opciones.length + 1} y presioná Enter...`}
                                className="flex-1 w-full"
                            />
                            <Button label="Agregar" icon="pi pi-plus" severity="secondary" outlined onClick={agregarOpcion} className="w-full sm:w-auto" />
                        </div>
                    </div>
                </div>

                <div className="flex justify-content-end gap-3 border-top-1 surface-border pt-4 mt-2">
                    <Button label="Cancelar" icon="pi pi-times" severity="secondary" text onClick={() => navigate('/preguntas')} />
                    <Button label="Guardar Pregunta" icon="pi pi-check" onClick={manejarGuardar} />
                </div>
            </div>

            {/* Modal IA */}
            <Dialog
                header={<div className="flex align-items-center gap-2"><i className="pi pi-sparkles text-primary text-xl"></i><span>Generar con IA</span></div>}
                visible={modalIaVisible}
                breakpoints={{ '960px': '75vw', '640px': '95vw' }}
                style={{ width: '45vw' }}
                footer={pieModalIa}
                onHide={() => !generandoIa && setModalIaVisible(false)}
            >
                <div className="pt-2 flex flex-column gap-3">
                    <p className="text-color-secondary text-sm m-0 line-height-3">
                        La IA utilizará el contexto previo (Competencia, Factor, Nombre y Descripción) para proponer una pregunta estructurada.
                    </p>
                    <div className="flex flex-column gap-2">
                        <label className="text-sm font-semibold text-color">Instrucciones Adicionales (Opcional)</label>
                        <InputTextarea
                            value={contextoExtra}
                            onChange={(e) => setContextoExtra(e.target.value)}
                            rows={4}
                            placeholder="Ej: Asegurate de que evalúe manejo de concurrencia y buenas prácticas..."
                            className="w-full resize-none"
                            disabled={generandoIa}
                        />
                    </div>
                </div>
            </Dialog>
        </div>
    );
};

export const CreateQuestion = CrearPregunta;
export default CrearPregunta;
