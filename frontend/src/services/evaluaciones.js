import api from '../api';

export const evaluacionesService = {
    async validarCandidatos(idsCandidatos) {
        await api.post('/api/evaluaciones/validar-candidatos', idsCandidatos);
    },

    async generar({ idPuesto, idsCandidatos }) {
        const { data } = await api.post('/api/evaluaciones/generar', { idPuesto, idsCandidatos });
        return data;
    }
};

export default evaluacionesService;
