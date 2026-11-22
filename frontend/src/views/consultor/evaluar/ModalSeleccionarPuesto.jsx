import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { Tag } from 'primereact/tag';
import { Button } from 'primereact/button';

export default function ModalSeleccionarPuesto({
    visible,
    alCerrar,
    alAvanzar,
    puestoSeleccionado,
    alSeleccionarPuesto,
    listaPuestos
}) {
    const competenciasInvalidas = puestoSeleccionado?.competencias?.filter((c) => c.cumpleCondicion === false) || [];
    const deshabilitarSiguiente = !puestoSeleccionado || competenciasInvalidas.length > 0;

    return (
        <Dialog
            header="Seleccionar Puesto"
            visible={visible}
            breakpoints={{ '960px': '75vw', '640px': '95vw' }}
            style={{ width: '45vw' }}
            onHide={alCerrar}
            footer={
                <div className="flex justify-content-end gap-2 pt-4 border-top-1 surface-border mt-3">
                    <Button label="Cancelar" severity="secondary" text onClick={alCerrar} />
                    <Button
                        label="Siguiente"
                        icon="pi pi-arrow-right"
                        iconPos="right"
                        onClick={alAvanzar}
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
                        onChange={(e) => alSeleccionarPuesto(e.value)}
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
    );
}
