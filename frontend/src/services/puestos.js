import api from '../api';

export const puestosService = {
    async obtenerParaSelect() {
        const { data } = await api.get('/api/puestos/select');
        return data;
    }
};

export default puestosService;
