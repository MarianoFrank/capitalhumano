import { Menu } from 'primereact/menu';
import { useNavigate, useLocation } from 'react-router-dom';
import { Avatar } from 'primereact/avatar';
import { Button } from 'primereact/button';
import { useAutenticacion, useTema } from '../context/ContextoApp';

export const BarraLateral = () => {
    const { esOscuro, alternarTema } = useTema();
    const navigate = useNavigate();
    const location = useLocation();
    const { usuario, cerrarSesion } = useAutenticacion();

    const estaActivo = (ruta) => location.pathname === ruta;

    const plantillaElemento = (item) => {
        const activo = estaActivo(item.route);

        return (
            <div
                onClick={() => item.route && navigate(item.route)}
                className={`flex align-items-center gap-3 px-3 py-2 mx-2 my-1 border-round cursor-pointer transition-colors transition-duration-200
                    ${activo ? 'surface-hover text-primary font-semibold' : 'text-color-secondary hover:surface-hover hover:text-color'}`}
            >
                <i className={`${item.icon} text-lg ${activo ? 'text-primary' : 'text-color-secondary'}`}></i>
                <span className="text-sm font-medium line-height-1">{item.label}</span>
            </div>
        );
    };

    const elementosConsultor = [
        {
            label: 'EVALUACIONES',
            items: [
                { label: 'Evaluar Candidatos', icon: 'pi pi-users', route: '/evaluar', template: plantillaElemento }
            ]
        },
        {
            label: 'REPORTES',
            items: [
                { label: 'Orden de mérito', icon: 'pi pi-list', route: '/reportes/orden-merito', template: plantillaElemento }
            ]
        },
        {
            label: 'PARÁMETROS DEL SISTEMA',
            items: [
                { label: 'Preguntas', icon: 'pi pi-question-circle', route: '/preguntas', template: plantillaElemento }
            ]
        }
    ];

    const iniciales = (usuario?.nombre?.charAt(0) || usuario?.name?.charAt(0) || '') +
                     (usuario?.apellido?.charAt(0) || usuario?.lastName?.charAt(0) || 'C');

    const nombreCompleto = (usuario?.nombre || usuario?.name || 'Consultor') + ' ' +
                           (usuario?.apellido || usuario?.lastname || usuario?.lastName || '');

    const legajo = usuario?.legajo || usuario?.username || '-';

    return (
        <div className="h-screen flex flex-column surface-card surface-border border-right-1 select-none" style={{ width: '260px', minWidth: '260px' }}>
            {/* --- LOGO --- */}
            <div className="p-4 hover:surface-hover transition-colors cursor-pointer" onClick={() => navigate('/inicio')}>
                <div className="flex align-items-center gap-3 px-1">
                    <div className="flex align-items-center justify-content-center w-3rem h-3rem border-round border-1 surface-border surface-ground">
                        <i className="pi pi-shield text-primary text-xl"></i>
                    </div>
                    <div className="flex flex-column">
                        <span className="text-lg font-bold text-color line-height-1">Capital Humano</span>
                    </div>
                </div>
            </div>

            {/* --- MENÚ --- */}
            <div className="flex-grow-1 overflow-auto pt-2">
                <Menu
                    model={elementosConsultor}
                    className="w-full border-none bg-transparent"
                    pt={{
                        submenuHeader: {
                            className: 'text-xs font-bold text-color-secondary px-4 pb-2 uppercase surface-ground',
                            style: { letterSpacing: '1px' }
                        },
                        menu: { className: 'border-none' }
                    }}
                />
            </div>

            {/* --- FOOTER DE USUARIO --- */}
            <div className="p-3 border-top-1 surface-border surface-ground">
                <div className="flex align-items-center justify-content-between px-1 gap-3">
                    <div className="flex align-items-center gap-3">
                        <div className="relative">
                            <Avatar
                                label={iniciales || 'C'}
                                size="large"
                                shape="circle"
                                className="bg-primary text-primary-contrast font-bold shadow-1"
                            />
                            <span className="absolute bottom-0 right-0 w-1rem h-1rem bg-green-500 border-2 border-circle" style={{ borderColor: 'var(--surface-ground)' }}></span>
                        </div>
                        <div className="flex flex-column" style={{ maxWidth: '110px' }}>
                            <span className="text-sm font-semibold text-color line-height-2 text-overflow-ellipsis white-space-nowrap overflow-hidden">
                                {nombreCompleto}
                            </span>
                            <span className="text-xs font-medium text-color-secondary">
                                Legajo: {legajo}
                            </span>
                        </div>
                    </div>

                    <div className="flex gap-1">
                        <Button
                            icon={esOscuro ? 'pi pi-sun' : 'pi pi-moon'}
                            severity="secondary"
                            onClick={alternarTema}
                            rounded
                            tooltipOptions={{ position: 'top' }}
                            tooltip={esOscuro ? 'Modo claro' : 'Modo oscuro'}
                            text
                        />
                        <Button
                            icon="pi pi-sign-out"
                            severity="secondary"
                            text
                            rounded
                            aria-label="Cerrar sesión"
                            onClick={cerrarSesion}
                            tooltip="Cerrar sesión"
                            tooltipOptions={{ position: 'top' }}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export const Sidebar = BarraLateral;
export default BarraLateral;
