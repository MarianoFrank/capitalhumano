import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { InputTextarea } from 'primereact/inputtextarea';

export default function ModalGeneracionIA({
    visible,
    alCerrar,
    contextoExtra,
    alCambiarContextoExtra,
    alGenerar,
    generando
}) {
    const pieModal = (
        <div className="flex justify-content-end gap-2 pt-3 border-top-1 surface-border mt-3">
            <Button label="Cancelar" icon="pi pi-times" severity="secondary" text onClick={alCerrar} disabled={generando} />
            <Button label={generando ? 'Generando...' : 'Generar IA'} icon="pi pi-sparkles" loading={generando} onClick={alGenerar} autoFocus />
        </div>
    );

    return (
        <Dialog
            header={
                <div className="flex align-items-center gap-2">
                    <i className="pi pi-sparkles text-primary text-xl"></i>
                    <span>Generar con IA</span>
                </div>
            }
            visible={visible}
            breakpoints={{ '960px': '75vw', '640px': '95vw' }}
            style={{ width: '45vw' }}
            footer={pieModal}
            onHide={() => !generando && alCerrar()}
        >
            <div className="pt-2 flex flex-column gap-3">
                <p className="text-color-secondary text-sm m-0 line-height-3">
                    La IA utilizará el contexto previo (Competencia, Factor, Nombre y Descripción) para proponer una pregunta estructurada.
                </p>
                <div className="flex flex-column gap-2">
                    <label className="text-sm font-semibold text-color">Instrucciones Adicionales (Opcional)</label>
                    <InputTextarea
                        value={contextoExtra}
                        onChange={(e) => alCambiarContextoExtra(e.target.value)}
                        rows={4}
                        placeholder="Ej: Asegurate de que evalúe manejo de concurrencia y buenas prácticas..."
                        className="w-full resize-none"
                        disabled={generando}
                    />
                </div>
            </div>
        </Dialog>
    );
}
