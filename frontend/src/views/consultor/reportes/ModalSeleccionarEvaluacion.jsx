import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';

export default function ModalSeleccionarEvaluacion({
    visible,
    alCerrar,
    codigoPuesto,
    evaluaciones,
    evaluacionSeleccionada,
    alSeleccionarEvaluacion,
    cargando,
    alGenerar
}) {
    return (
        <Dialog
            header={`Puesto ${codigoPuesto || ''}`}
            visible={visible}
            style={{ width: '450px' }}
            onHide={alCerrar}
        >
            <div className="flex flex-column gap-3 pt-2">
                <label className="font-medium text-color-secondary">
                    Seleccione una o todas las evaluaciones
                </label>
                <Dropdown
                    value={evaluacionSeleccionada}
                    options={evaluaciones}
                    onChange={(e) => alSeleccionarEvaluacion(e.value)}
                    optionLabel="descripcion"
                    optionValue="id"
                    placeholder="Todas"
                    showClear
                    loading={cargando}
                    className="w-full"
                />
                <div className="flex justify-content-end mt-3">
                    <Button label="Generar" onClick={alGenerar} />
                </div>
            </div>
        </Dialog>
    );
}
