import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { useAutenticacion } from '../hooks/useAutenticacion';

export default function InicioPublico() {
    const navigate = useNavigate();
    const { iniciarSesionCandidato } = useAutenticacion();

    const [claveAcceso, setClaveAcceso] = useState('');
    const [cargando, setCargando] = useState(false);
    const [error, setError] = useState('');

    const manejarAcceso = async (e) => {
        e.preventDefault();

        if (!claveAcceso.trim()) {
            setError('Por favor, ingresá un código válido.');
            return;
        }

        setError('');
        setCargando(true);

        try {
            const respuesta = await iniciarSesionCandidato(claveAcceso.trim());
            const idCuestionario = respuesta.questionnaireId || respuesta.idCuestionario;
            navigate(`/cuestionario/${idCuestionario}`);
        } catch (err) {
            const mensajeError = err.response?.data?.message || err.response?.data?.error || 'Código de acceso inválido o expirado.';
            setError(mensajeError);
        } finally {
            setCargando(false);
        }
    };

    return (
        <div className="flex flex-column align-items-center justify-content-center min-h-screen w-full surface-ground px-4 py-6">
            <div className="w-full" style={{ maxWidth: '420px' }}>
                <div className="text-center mb-5">
                    <div className="inline-flex align-items-center justify-content-center w-4rem h-4rem border-circle bg-primary-100 text-primary mb-3">
                        <i className="pi pi-verified text-3xl"></i>
                    </div>
                    <h1 className="text-3xl font-bold text-color m-0">Capital Humano</h1>
                    <p className="text-sm text-color-secondary mt-2 font-medium">Evaluación de Competencias TIC</p>
                </div>

                <div className="surface-card border-1 surface-border border-round p-5 flex flex-column">
                    <h2 className="text-xl font-bold text-color text-center mb-4 mt-0">Ingreso de Candidatos</h2>

                    <form onSubmit={manejarAcceso} className="flex flex-column gap-4">
                        <div className="flex flex-column gap-2">
                            <label htmlFor="code" className="text-sm font-semibold text-color">Código de Invitación</label>
                            <div className="p-inputgroup w-full">
                                <span className="p-inputgroup-addon surface-ground">
                                    <i className="pi pi-key text-color-secondary"></i>
                                </span>
                                <InputText
                                    id="code"
                                    value={claveAcceso}
                                    onChange={(e) => setClaveAcceso(e.target.value)}
                                    placeholder="Ej: ABC-123-XYZ"
                                    disabled={cargando}
                                    autoComplete="off"
                                />
                            </div>
                        </div>

                        {error && <Message severity="error" text={error} className="w-full justify-content-start text-sm" />}

                        <Button
                            label={cargando ? 'Verificando...' : 'Comenzar Evaluación'}
                            icon="pi pi-sign-in"
                            type="submit"
                            className="w-full mt-2"
                            loading={cargando}
                        />
                    </form>
                </div>

                <div
                    className="mt-4 surface-card border-1 surface-border border-round p-3 flex align-items-center justify-content-between hover:surface-hover transition-colors cursor-pointer"
                    onClick={() => navigate('/login')}
                >
                    <div className="flex align-items-center gap-3">
                        <div className="flex align-items-center justify-content-center w-2rem h-2rem border-circle surface-ground text-color-secondary">
                            <i className="pi pi-briefcase text-sm"></i>
                        </div>
                        <div>
                            <h3 className="text-sm font-semibold text-color m-0">¿Sos consultor?</h3>
                            <p className="text-xs text-color-secondary m-0 mt-1">Accedé al panel de gestión.</p>
                        </div>
                    </div>
                    <Button icon="pi pi-arrow-right" text rounded aria-label="Ingresar" />
                </div>
            </div>
        </div>
    );
}

export const PublicHome = InicioPublico;
