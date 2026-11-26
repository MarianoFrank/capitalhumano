import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { useMountEffect } from 'primereact/hooks';
import { preguntasService } from '../../../services/preguntas';
import { useToast } from '../../../context';
import { OPCIONES_TIPO_PREGUNTA, TIPOS_PREGUNTA } from '../../../constants';

// Subcomponentes modulares
import TablaOpcionesRespuesta from './TablaOpcionesRespuesta';
import ModalGeneracionIA from './ModalGeneracionIA';

export default function CrearPregunta() {
    const { mostrarExito, mostrarError } = useToast();
    const navigate = useNavigate();

    const [datosFormulario, setDatosFormulario] = useState({
        idCompetencia: null,
        idFactor: null,
        nombre: '',
        descripcion: '',
        texto: '',
        tipo: TIPOS_PREGUNTA.UNICA,
        opciones: []
    });

    const [textoNuevaOpcion, setTextoNuevaOpcion] = useState('');
    const [modalIaVisible, setModalIaVisible] = useState(false);
    const [contextoExtra, setContextoExtra] = useState('');
    const [generandoIa, setGenerandoIa] = useState(false);

    const [listaCompetencias, setListaCompetencias] = useState([]);
    const [listaFactores, setListaFactores] = useState([]);

    useMountEffect(() => {
        preguntasService.obtenerCompetenciasSelect()
            .then((data) => setListaCompetencias(data))
            .catch(() => mostrarError('No se pudieron cargar las competencias.'));
    });

    const manejarCambioCompetencia = (e) => {
        const idSeleccionado = e.value;
        setDatosFormulario((prev) => ({ ...prev, idCompetencia: idSeleccionado, idFactor: null }));
        setListaFactores([]);

        if (idSeleccionado) {
            preguntasService.obtenerFactoresSelect(idSeleccionado)
                .then((data) => setListaFactores(data))
                .catch(() => mostrarError('No se pudieron cargar los factores.'));
        }
    };

    const agregarOpcion = () => {
        if (!textoNuevaOpcion.trim()) return;
        setDatosFormulario((prev) => ({
            ...prev,
            opciones: [...prev.opciones, { texto: textoNuevaOpcion, ponderacion: 0 }]
        }));
        setTextoNuevaOpcion('');
    };

    const quitarOpcion = (indiceAEliminar) => {
        setDatosFormulario((prev) => ({
            ...prev,
            opciones: prev.opciones.filter((_, idx) => idx !== indiceAEliminar)
        }));
    };

    const manejarCambioPonderacion = (indice, nuevaPonderacion) => {
        setDatosFormulario((prev) => {
            const opcionesActualizadas = [...prev.opciones];
            opcionesActualizadas[indice].ponderacion = nuevaPonderacion;
            return { ...prev, opciones: opcionesActualizadas };
        });
    };

    const manejarCambioTextoOpcion = (indice, nuevoTexto) => {
        setDatosFormulario((prev) => {
            const opcionesActualizadas = [...prev.opciones];
            opcionesActualizadas[indice].texto = nuevoTexto;
            return { ...prev, opciones: opcionesActualizadas };
        });
    };

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
            await preguntasService.crearPregunta(payload);
            mostrarExito('¡Pregunta creada con éxito!');
            setTimeout(() => navigate('/preguntas'), 1200);
        } catch (error) {
            mostrarError(error.response?.data?.message || 'Hubo un error al guardar la pregunta.');
        }
    };

    const manejarGenerarConIa = async () => {
        const comp = listaCompetencias.find((c) => c.id === datosFormulario.idCompetencia);
        const fact = listaFactores.find((f) => f.id === datosFormulario.idFactor);

        if (!comp || !fact) return mostrarError('Seleccioná una competencia y un factor primero.');

        setGenerandoIa(true);
        try {
            const respuesta = await preguntasService.generarConIa({
                nombreCompetencia: comp.nombre,
                nombreFactor: fact.nombre,
                nombrePregunta: datosFormulario.nombre,
                descripcion: datosFormulario.descripcion,
                contextoExtra: contextoExtra
            });

            setDatosFormulario((prev) => ({
                ...prev,
                nombre: respuesta.nombrePregunta || prev.nombre,
                descripcion: respuesta.descripcion || prev.descripcion,
                tipo: respuesta.tipo || prev.tipo,
                texto: respuesta.texto,
                opciones: (respuesta.opciones || []).map((o) => ({
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

    return (
        <div className="w-full flex flex-column gap-4 pb-8">
            <div className="flex align-items-center gap-3 mb-2">
                <i className="pi pi-plus-circle text-3xl text-primary"></i>
                <div>
                    <h1 className="m-0 text-2xl font-bold text-color">Crear Pregunta</h1>
                    <p className="m-0 mt-1 text-sm text-color-secondary">Añadí un nuevo escenario al banco de evaluación</p>
                </div>
            </div>

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
                            onChange={(e) => setDatosFormulario((prev) => ({ ...prev, idFactor: e.value }))}
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
                        onChange={(e) => setDatosFormulario((prev) => ({ ...prev, nombre: e.target.value }))}
                        placeholder="Ej. Resolución de consultas SQL lentas"
                        className="w-full"
                    />
                </div>

                <div className="flex flex-column gap-2">
                    <label className="font-semibold text-sm text-color-secondary">Descripción (opcional)</label>
                    <InputTextarea
                        value={datosFormulario.descripcion}
                        onChange={(e) => setDatosFormulario((prev) => ({ ...prev, descripcion: e.target.value }))}
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
                            onChange={(e) => setDatosFormulario((prev) => ({ ...prev, texto: e.target.value }))}
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
                        options={OPCIONES_TIPO_PREGUNTA}
                        onChange={(e) => setDatosFormulario((prev) => ({ ...prev, tipo: e.value }))}
                        placeholder="Seleccione un tipo"
                        className="w-full md:w-20rem"
                    />
                </div>

                <TablaOpcionesRespuesta
                    opciones={datosFormulario.opciones}
                    alReordenar={(nuevas) => setDatosFormulario((prev) => ({ ...prev, opciones: nuevas }))}
                    alCambiarTexto={manejarCambioTextoOpcion}
                    alCambiarPonderacion={manejarCambioPonderacion}
                    alQuitarOpcion={quitarOpcion}
                    textoNuevaOpcion={textoNuevaOpcion}
                    alCambiarTextoNuevaOpcion={setTextoNuevaOpcion}
                    alAgregarOpcion={agregarOpcion}
                />

                <div className="flex justify-content-end gap-3 border-top-1 surface-border pt-4 mt-2">
                    <Button label="Cancelar" icon="pi pi-times" severity="secondary" text onClick={() => navigate('/preguntas')} />
                    <Button label="Guardar Pregunta" icon="pi pi-check" onClick={manejarGuardar} />
                </div>
            </div>

            <ModalGeneracionIA
                visible={modalIaVisible}
                alCerrar={() => setModalIaVisible(false)}
                contextoExtra={contextoExtra}
                alCambiarContextoExtra={setContextoExtra}
                alGenerar={manejarGenerarConIa}
                generando={generandoIa}
            />
        </div>
    );
}
