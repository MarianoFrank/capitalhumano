import api from '../api';

export const preguntasService = {
    async obtenerPaginadas({ page = 0, size = 20, sort = 'fechaModificacion,desc', idCompetencia, idFactor, nombrePregunta }) {
        const params = {
            page,
            size,
            sort,
            ...(idCompetencia ? { idCompetencia } : {}),
            ...(idFactor ? { idFactor } : {}),
            ...(nombrePregunta ? { nombrePregunta } : {})
        };
        const { data } = await api.get('/api/preguntas', { params });
        return data;
    },

    async obtenerCompetenciasSelect() {
        const { data } = await api.get('/api/competencias/select');
        return data;
    },

    async obtenerFactoresSelect(idCompetencia) {
        const { data } = await api.get(`/api/factores/select?idCompetencia=${idCompetencia}`);
        return data;
    },

    async crearPregunta(payload) {
        const { data } = await api.post('/api/preguntas', payload);
        return data;
    },

    async generarConIa(payload) {
        const { data } = await api.post('/api/ia/generar-pregunta', payload);
        return data;
    }
};

export default preguntasService;
