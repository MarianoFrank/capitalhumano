import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

export default function ModalConfirmarEvaluacion({
    visible,
    alCerrar,
    alAtras,
    alConfirmar,
    puestoSeleccionado,
    candidatosSeleccionados,
    generando
}) {
    return (
        <Dialog
            header="Confirmar Generación"
            visible={visible}
            breakpoints={{ '960px': '75vw', '640px': '95vw' }}
            style={{ width: '50vw' }}
            closable={!generando}
            onHide={alCerrar}
            footer={
                <div className="flex justify-content-end gap-2 pt-4 border-top-1 surface-border mt-3">
                    <Button label="Atrás" severity="secondary" text disabled={generando} onClick={alAtras} />
                    <Button
                        label="Finalizar y Generar"
                        icon="pi pi-check"
                        severity="success"
                        loading={generando}
                        onClick={alConfirmar}
                    />
                </div>
            }
        >
            <p className="m-0 mb-4 text-color-secondary">
                Estás por generar la evaluación del puesto <strong>{puestoSeleccionado?.nombre}</strong> para {candidatosSeleccionados.length} candidatos.
            </p>

            <div className="border-1 surface-border border-round overflow-hidden">
                <DataTable
                    value={candidatosSeleccionados}
                    dataKey="id"
                    className="p-datatable-sm"
                    rowHover
                    scrollable
                    scrollHeight="350px"
                    pt={{ wrapper: { className: 'border-none' } }}
                >
                    <Column field="nombre" header="Nombre" className="font-medium" />
                    <Column field="apellido" header="Apellido" className="font-medium" />
                    <Column field="numeroCandidato" header="Número Candidato" className="text-color-secondary" />
                </DataTable>
            </div>
        </Dialog>
    );
}
