import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Button } from 'primereact/button';

export default function TablaOpcionesRespuesta({
    opciones,
    alReordenar,
    alCambiarTexto,
    alCambiarPonderacion,
    alQuitarOpcion,
    textoNuevaOpcion,
    alCambiarTextoNuevaOpcion,
    alAgregarOpcion
}) {
    return (
        <div className="flex flex-column gap-3">
            <label className="font-semibold text-sm text-color-secondary">
                Opciones de Respuesta (Arrastrá para ordenar)
            </label>

            <div className="border-1 surface-border border-round overflow-hidden flex flex-column">
                <DataTable
                    showHeaders={false}
                    className="shadow-none border-none p-datatable-sm"
                    value={opciones}
                    reorderableRows
                    onRowReorder={(e) => alReordenar(e.value)}
                    emptyMessage={<div className="p-4 text-center text-color-secondary">Todavía no agregaste ninguna opción.</div>}
                >
                    <Column rowReorder style={{ width: '3rem', textAlign: 'center' }} />

                    <Column
                        body={(fila, props) => <span className="font-medium text-color-secondary">{props.rowIndex + 1}.</span>}
                        style={{ width: '3rem' }}
                    />

                    <Column
                        body={(fila, props) => (
                            <InputText
                                value={fila.texto}
                                onChange={(e) => alCambiarTexto(props.rowIndex, e.target.value)}
                                className="w-full"
                                placeholder="Escribí la opción..."
                            />
                        )}
                    />

                    <Column
                        body={(fila, props) => (
                            <div className="flex align-items-center justify-content-end gap-2">
                                <span className="text-xs text-color-secondary font-semibold uppercase tracking-wider">Ponderación</span>
                                <InputNumber
                                    value={fila.ponderacion}
                                    onValueChange={(e) => alCambiarPonderacion(props.rowIndex, e.value)}
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
                        )}
                        style={{ width: '16rem' }}
                    />

                    <Column
                        body={(fila, props) => (
                            <Button
                                icon="pi pi-trash"
                                severity="danger"
                                text
                                rounded
                                aria-label="Eliminar"
                                onClick={() => alQuitarOpcion(props.rowIndex)}
                            />
                        )}
                        style={{ width: '4rem', textAlign: 'center' }}
                    />
                </DataTable>

                <div className="surface-ground p-3 surface-border flex flex-column sm:flex-row align-items-center gap-3">
                    <InputText
                        value={textoNuevaOpcion}
                        onChange={(e) => alCambiarTextoNuevaOpcion(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter' && !e.shiftKey) {
                                e.preventDefault();
                                alAgregarOpcion();
                            }
                        }}
                        placeholder={`Escribí la opción ${opciones.length + 1} y presioná Enter...`}
                        className="flex-1 w-full"
                    />
                    <Button label="Agregar" icon="pi pi-plus" severity="secondary" outlined onClick={alAgregarOpcion} className="w-full sm:w-auto" />
                </div>
            </div>
        </div>
    );
}
