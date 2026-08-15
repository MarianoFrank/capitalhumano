import { RadioButton } from 'primereact/radiobutton';
import { Checkbox } from 'primereact/checkbox';
import { Divider } from 'primereact/divider';

export default function BloquePreguntas({ bloque, respuestas, alSeleccionarOpcion }) {
    const itemsPregunta = bloque?.itemsPregunta || bloque?.questionItems;
    if (!bloque || !itemsPregunta) return null;

    return (
        <div className="flex flex-column gap-5">
            {itemsPregunta.map((item, index) => {
                const esMultiple = item.esMultiple != null ? item.esMultiple : item.multiple;
                const textoItem = item.texto || item.text;
                const itemsOpcion = item.itemsOpcion || item.optionItems || [];

                const respuestaActual = respuestas[item.id] || (esMultiple ? [] : null);

                const manejarCambioCheckbox = (idOpcion) => {
                    let seleccionActualizada = Array.isArray(respuestaActual) ? [...respuestaActual] : [];

                    if (seleccionActualizada.includes(idOpcion)) {
                        seleccionActualizada = seleccionActualizada.filter(id => id !== idOpcion);
                    } else {
                        seleccionActualizada.push(idOpcion);
                    }

                    alSeleccionarOpcion(item.id, seleccionActualizada);
                };

                const manejarCambioRadio = (idOpcion) => {
                    alSeleccionarOpcion(item.id, idOpcion);
                };

                return (
                    <div key={item.id} className="flex flex-column gap-3">
                        <div className="flex align-items-start gap-2">
                            <span className="font-bold text-primary text-lg">{index + 1}.</span>
                            <div>
                                <h3 className="m-0 text-lg font-medium text-color line-height-3">
                                    {textoItem}
                                </h3>
                                <span className="text-xs text-color-secondary font-semibold">
                                    {esMultiple ? '☑ Selección múltiple (Podés marcar varias opciones)' : '🔘 Selección única (Una sola opción)'}
                                </span>
                            </div>
                        </div>

                        {/* Opciones de respuesta */}
                        <div className="flex flex-column gap-2 pl-4">
                            {itemsOpcion.map((optItem) => {
                                const idOpcion = optItem.id;
                                const textoOpcion = optItem.texto || optItem.text;
                                const inputId = `opt-${optItem.id}`;

                                return (
                                    <div
                                        key={optItem.id}
                                        className="flex align-items-center gap-3 p-2 hover:surface-hover border-round cursor-pointer"
                                    >
                                        {esMultiple ? (
                                            <Checkbox
                                                inputId={inputId}
                                                name={`pregunta-${item.id}`}
                                                value={idOpcion}
                                                checked={Array.isArray(respuestaActual) && respuestaActual.includes(idOpcion)}
                                                onChange={() => manejarCambioCheckbox(idOpcion)}
                                            />
                                        ) : (
                                            <RadioButton
                                                inputId={inputId}
                                                name={`pregunta-${item.id}`}
                                                value={idOpcion}
                                                checked={respuestaActual === idOpcion}
                                                onChange={() => manejarCambioRadio(idOpcion)}
                                            />
                                        )}
                                        <label htmlFor={inputId} className="text-color text-sm cursor-pointer w-full select-none">
                                            {textoOpcion}
                                        </label>
                                    </div>
                                );
                            })}
                        </div>

                        {index < itemsPregunta.length - 1 && <Divider className="my-2" />}
                    </div>
                );
            })}
        </div>
    );
}

export const QuestionBlock = BloquePreguntas;
