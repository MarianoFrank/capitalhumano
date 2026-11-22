import { Tag } from 'primereact/tag';
import { Button } from 'primereact/button';
import { Divider } from 'primereact/divider';

export default function BarraSeleccionados({
    cantidadSeleccionados,
    alAbrirPanel,
    alLimpiar,
    alContinuarPuesto
}) {
    if (cantidadSeleccionados === 0) return null;

    return (
        <div className="fixed bottom-0 left-0 right-0 glass-card py-3 px-4 shadow-3 z-5 flex flex-column sm:flex-row align-items-center justify-content-between gap-3 border-top-1 surface-border fadein">
            <div className="flex align-items-center gap-3">
                <Tag severity="success" value={cantidadSeleccionados} rounded className="px-3 py-2" icon="pi pi-check" />
                <span className="font-medium text-color-secondary">candidatos seleccionados</span>
            </div>

            <div className="flex align-items-center gap-2 w-full sm:w-auto justify-content-end">
                <Button label="Ver lista" icon="pi pi-list" severity="secondary" text onClick={alAbrirPanel} />
                <Button label="Limpiar" icon="pi pi-trash" severity="danger" text onClick={alLimpiar} />
                <Divider layout="vertical" className="mx-2" />
                <Button label="Siguiente: Puesto" icon="pi pi-arrow-right" iconPos="right" onClick={alContinuarPuesto} />
            </div>
        </div>
    );
}
