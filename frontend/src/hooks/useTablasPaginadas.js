import { useState, useCallback } from 'react';

export const useTablasPaginadas = ({ campoOrdenPorDefecto = 'id', ordenPorDefecto = 1, filasPorDefecto = 20, defaultSortField = 'id', defaultSortOrder = 1, defaultRows = 20 } = {}) => {
    const campoInicial = campoOrdenPorDefecto !== 'id' ? campoOrdenPorDefecto : defaultSortField;
    const ordenInicial = ordenPorDefecto !== 1 ? ordenPorDefecto : defaultSortOrder;
    const filasIniciales = filasPorDefecto !== 20 ? filasPorDefecto : defaultRows;

    const [totalRegistros, setTotalRegistros] = useState(0);
    const [parametrosPaginacion, setParametrosPaginacion] = useState({
        first: 0,
        rows: filasIniciales,
        page: 0,
        sortField: campoInicial,
        sortOrder: ordenInicial
    });

    const alCambiarPagina = useCallback((e) => {
        setParametrosPaginacion(prev => ({ ...prev, first: e.first, rows: e.rows, page: e.page }));
    }, []);

    const alCambiarOrden = useCallback((e) => {
        setParametrosPaginacion(prev => ({ ...prev, sortField: e.sortField, sortOrder: e.sortOrder }));
    }, []);

    // Para reiniciar la paginación al filtrar
    const reiniciarPaginacion = useCallback(() => {
        setParametrosPaginacion(prev => ({ ...prev, first: 0, page: 0 }));
    }, []);

    return {
        // En español
        parametrosPaginacion,
        setParametrosPaginacion,
        totalRegistros,
        setTotalRegistros,
        alCambiarPagina,
        alCambiarOrden,
        reiniciarPaginacion,

        // Alias compatibles
        lazyParams: parametrosPaginacion,
        setLazyParams: setParametrosPaginacion,
        totalRecords: totalRegistros,
        setTotalRecords: setTotalRegistros,
        onPage: alCambiarPagina,
        onSort: alCambiarOrden,
        resetPagination: reiniciarPaginacion
    };
};

export const useLazyTable = useTablasPaginadas;
export default useTablasPaginadas;
