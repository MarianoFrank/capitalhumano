import { useState, useDeferredValue } from 'react';
import { Sidebar } from 'primereact/sidebar';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';

export default function PanelCandidatosSeleccionados({
    visible,
    alCerrar,
    candidatosSeleccionados,
    alQuitarCandidato
}) {
    const [busqueda, setBusqueda] = useState('');
    const busquedaDiferida = useDeferredValue(busqueda);

    const candidatosFiltrados = candidatosSeleccionados.filter((c) => {
        const query = busquedaDiferida.toLowerCase();
        const nombreCompleto = `${c.nombre} ${c.apellido}`.toLowerCase();
        const numeroStr = String(c.numeroCandidato || '').toLowerCase();
        return nombreCompleto.includes(query) || numeroStr.includes(query);
    });

    const plantillaCandidato = (fila) => (
        <div className="flex flex-column gap-1">
            <span className="font-semibold text-sm">{fila.nombre} {fila.apellido}</span>
            <span className="text-xs text-color-secondary">Nro: {fila.numeroCandidato}</span>
        </div>
    );

    const plantillaAccion = (fila) => (
        <Button
            icon="pi pi-times"
            severity="danger"
            text
            rounded
            aria-label="Quitar"
            onClick={() => alQuitarCandidato(fila.id)}
        />
    );

    return (
        <Sidebar
            visible={visible}
            position="right"
            onHide={alCerrar}
            className="w-full md:w-30rem"
            header={<div className="font-bold text-xl">Candidatos Seleccionados</div>}
        >
            <div className="flex flex-column h-full pb-3">
                <p className="text-color-secondary mb-3 text-sm flex-none">
                    Revisá la lista de postulantes elegidos antes de continuar.
                </p>

                <div className="mb-3 flex-none">
                    <IconField iconPosition="left" className="w-full">
                        <InputIcon className="pi pi-search" />
                        <InputText
                            value={busqueda}
                            onChange={(e) => setBusqueda(e.target.value)}
                            placeholder="Buscar por nombre, apellido o número..."
                            className="w-full"
                        />
                    </IconField>
                </div>

                <div className="border-1 surface-border border-round overflow-hidden">
                    <DataTable
                        value={candidatosFiltrados}
                        dataKey="id"
                        emptyMessage={<div className="p-4 text-center text-color-secondary text-sm">No se encontraron candidatos.</div>}
                        size="small"
                        rowHover
                        className="p-datatable-sm"
                        scrollable
                        scrollHeight="calc(100vh - 290px)"
                        showHeaders={false}
                        pt={{ wrapper: { className: 'border-none' } }}
                    >
                        <Column body={plantillaCandidato} header="Postulante" />
                        <Column body={plantillaAccion} headerStyle={{ width: '4rem' }} bodyStyle={{ textAlign: 'center' }} />
                    </DataTable>
                </div>

                <div className="mt-auto pt-3 surface-border flex-none">
                    <Button label="Cerrar panel" severity="secondary" text className="w-full" onClick={alCerrar} />
                </div>
            </div>
        </Sidebar>
    );
}
