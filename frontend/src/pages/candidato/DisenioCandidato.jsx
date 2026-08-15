import { Outlet, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Avatar } from 'primereact/avatar';
import { useAutenticacion, useTema } from '../../context/ContextoApp';

export default function DisenioCandidato() {
    const { esOscuro, alternarTema } = useTema();
    const navigate = useNavigate();
    const { usuario: candidato } = useAutenticacion();

    const nombreCandidato = candidato?.nombre || candidato?.firstName || '';
    const inicial = nombreCandidato ? nombreCandidato.charAt(0).toUpperCase() : 'C';

    return (
        <div className="min-h-screen surface-ground flex flex-column">
            <header className="surface-card shadow-1 px-4 py-3 flex align-items-center justify-content-between z-1 border-bottom-1 surface-border">
                {/* Logo y Título */}
                <div className="flex align-items-center gap-2 text-primary cursor-pointer" onClick={() => navigate('/')}>
                    <i className="pi pi-verified text-2xl"></i>
                    <span className="font-bold text-xl">Capital Humano</span>
                </div>

                {/* Sección Derecha */}
                <div className="flex align-items-center gap-3 text-color-secondary text-sm font-medium">
                    {nombreCandidato && (
                        <div className="hidden md:flex align-items-center gap-2 mr-2 border-right-1 surface-border pr-3">
                            <Avatar
                                label={inicial}
                                shape="circle"
                                className="bg-primary text-white font-bold"
                            />
                            <span>¡Hola, {nombreCandidato}!</span>
                        </div>
                    )}

                    <span className="hidden sm:inline">Evaluación de Competencias TIC</span>

                    <Button
                        icon={esOscuro ? 'pi pi-sun' : 'pi pi-moon'}
                        severity="secondary"
                        onClick={alternarTema}
                        outlined
                        rounded
                        tooltipOptions={{ position: 'top' }}
                        tooltip={esOscuro ? 'Modo claro' : 'Modo oscuro'}
                        text
                        className="ml-2"
                    />
                </div>
            </header>

            {/* Contenedor central */}
            <main className="flex-1 flex flex-column p-3 sm:p-5 w-full" style={{ maxWidth: '900px', margin: '0 auto' }}>
                <Outlet />
            </main>
        </div>
    );
}

export const CandidateLayout = DisenioCandidato;
