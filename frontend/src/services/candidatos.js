import api from '../api';

export const candidatosService = {
    async obtenerPaginados({ page = 0, size = 20, sort = 'numeroCandidato,asc', nombre, apellido, numeroCandidato }) {
        const params = {
            page,
            size,
            sort,
            ...(nombre ? { nombre } : {}),
            ...(apellido ? { apellido } : {}),
            ...(numeroCandidato ? { numeroCandidato } : {})
        };
        const { data } = await api.get('/api/candidatos', { params });
        return data;
    },

    async cargarCsv(archivo) {
        const formData = new FormData();
        formData.append('archivo', archivo);
        const { data } = await api.post('/api/candidatos/cargar-csv', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        return data;
    }
};

export default candidatosService;
