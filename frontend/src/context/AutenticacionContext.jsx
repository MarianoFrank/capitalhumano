import { createContext } from 'react';

// Contexto principal de autenticación
export const AutenticacionContext = createContext(null);
export const AuthContext = AutenticacionContext;

export default AutenticacionContext;
