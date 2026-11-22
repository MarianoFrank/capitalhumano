export default function Cargando({ mensaje = 'Cargando...' }) {
    return (
        <div className="flex flex-column align-items-center justify-content-center min-h-screen surface-ground p-4">
            <i className="pi pi-spin pi-spinner text-4xl text-primary mb-3"></i>
            <span className="text-color-secondary font-medium">{mensaje}</span>
        </div>
    );
}
