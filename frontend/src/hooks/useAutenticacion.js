import { useContext } from 'react';
import { AutenticacionContext } from '../context/AutenticacionContext';

export const useAutenticacion = () => {
    const context = useContext(AutenticacionContext);
    if (!context) {
        throw new Error("useAutenticacion debe ser usado dentro de un AutenticacionProvider");
    }
    return context;
};

export const useAuth = useAutenticacion;
export default useAutenticacion;
