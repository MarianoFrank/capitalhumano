import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';

export default function ModalDescargaExcel({
    visible,
    alOmitir,
    alDescargar
}) {
    return (
        <Dialog
            visible={visible}
            breakpoints={{ '960px': '75vw', '640px': '95vw' }}
            style={{ width: '30vw' }}
            onHide={alOmitir}
            showHeader={false}
            closable={false}
        >
            <div className="flex flex-column align-items-center gap-3 pt-5 pb-2 text-center">
                <div className="flex align-items-center justify-content-center surface-100 border-circle w-4rem h-4rem mb-2">
                    <i className="pi pi-file-excel text-green-500 text-4xl"></i>
                </div>
                <h2 className="m-0 text-xl font-bold text-color">Evaluación Lista</h2>
                <p className="m-0 text-color-secondary line-height-3 px-3">
                    ¿Deseás descargar la lista de candidatos con sus claves de acceso en un archivo Excel?
                </p>
                <div className="flex gap-3 mt-4 w-full">
                    <Button label="No, gracias" severity="secondary" outlined className="flex-1" onClick={alOmitir} />
                    <Button label="Descargar Excel" severity="success" className="flex-1" onClick={alDescargar} />
                </div>
            </div>
        </Dialog>
    );
}
