import api from '../api';

export const cuestionariosService = {
    async iniciar(idCuestionario) {
        const { data } = await api.post(`/api/cuestionarios/${idCuestionario}/iniciar`);
        return data;
    },

    async obtenerBloque(idCuestionario, numeroBloque) {
        const { data } = await api.get(`/api/cuestionarios/${idCuestionario}/bloques/${numeroBloque}`);
        return data;
    },

    async responderBloque(idCuestionario, numeroBloque, respuestas) {
        const { data } = await api.post(`/api/cuestionarios/${idCuestionario}/bloques/${numeroBloque}/responder`, respuestas);
        return data;
    },

    async expirar(idCuestionario) {
        await api.post(`/api/cuestionarios/${idCuestionario}/expirar`);
    }
};

export default cuestionariosService;
