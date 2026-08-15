import { Outlet } from 'react-router-dom';
import BarraLateral from '../../components/BarraLateral';

export const DisenioConsultor = () => {
    return (
        <div className="flex w-full h-screen overflow-hidden surface-ground">
            <BarraLateral />

            {/* Contenedor principal con scroll interno */}
            <div className="flex-1 flex flex-column h-screen overflow-hidden">
                <main className="flex-1 overflow-y-auto p-4 md:p-6 lg:px-8 lg:py-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export const DashboardLayout = DisenioConsultor;
export default DisenioConsultor;
