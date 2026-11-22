import api from '../api';

export const authService = {
    async obtenerPerfil() {
        const { data } = await api.get('/api/autenticacion/perfil');
        return data;
    },

    async iniciarSesion(usuario, contrasenia) {
        const { data } = await api.post('/api/autenticacion/iniciar-sesion', { usuario, contrasenia });
        return data;
    },

    async iniciarSesionCandidato(claveAcceso) {
        const { data } = await api.post('/api/autenticacion/candidato/iniciar-sesion', { claveAcceso });
        return data;
    },

    async cerrarSesion() {
        const { data } = await api.post('/api/autenticacion/cerrar-sesion');
        return data;
    }
};

export default authService;
