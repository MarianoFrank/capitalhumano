import api from '../api';

export const reportesService = {
    async obtenerEmpresasSelect() {
        const { data } = await api.get('/api/empresas/select');
        return data;
    },

    async obtenerPuestosPaginados({ page = 0, size = 20, idEmpresa, nombrePuesto, codigo }) {
        const params = {
            page,
            size,
            ...(idEmpresa ? { idEmpresa } : {}),
            ...(nombrePuesto ? { nombrePuesto } : {}),
            ...(codigo ? { codigo } : {})
        };
        const { data } = await api.get('/api/reportes/puestos', { params });
        return data;
    },

    async obtenerEvaluacionesPorPuesto(idPuesto) {
        const { data } = await api.get(`/api/reportes/puestos/${idPuesto}/evaluaciones`);
        return data;
    },

    async obtenerOrdenMerito(idPuesto, idEvaluacion = null) {
        const endpoint = idEvaluacion
            ? `/api/reportes/orden-merito/${idPuesto}?idEvaluacion=${idEvaluacion}`
            : `/api/reportes/orden-merito/${idPuesto}`;
        const { data } = await api.get(endpoint);
        return data;
    }
};

export default reportesService;
