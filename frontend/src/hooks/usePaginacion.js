import { useState, useCallback } from 'react';

/**
 * Hook reutilizable para control de paginación y ordenamiento en componentes DataTable de PrimeReact.
 */
export const usePaginacion = ({
    campoOrdenPorDefecto = 'id',
    ordenPorDefecto = 1,
    filasPorDefecto = 20
} = {}) => {
    const [totalRegistros, setTotalRegistros] = useState(0);
    const [parametrosPaginacion, setParametrosPaginacion] = useState({
        first: 0,
        rows: filasPorDefecto,
        page: 0,
        sortField: campoOrdenPorDefecto,
        sortOrder: ordenPorDefecto
    });

    const alCambiarPagina = useCallback((e) => {
        setParametrosPaginacion((prev) => ({ ...prev, first: e.first, rows: e.rows, page: e.page }));
    }, []);

    const alCambiarOrden = useCallback((e) => {
        setParametrosPaginacion((prev) => ({ ...prev, sortField: e.sortField, sortOrder: e.sortOrder }));
    }, []);

    const reiniciarPaginacion = useCallback(() => {
        setParametrosPaginacion((prev) => ({ ...prev, first: 0, page: 0 }));
    }, []);

    return {
        parametrosPaginacion,
        setParametrosPaginacion,
        totalRegistros,
        setTotalRegistros,
        alCambiarPagina,
        alCambiarOrden,
        reiniciarPaginacion,

        // Alias de compatibilidad
        lazyParams: parametrosPaginacion,
        setLazyParams: setParametrosPaginacion,
        totalRecords: totalRegistros,
        setTotalRecords: setTotalRegistros,
        onPage: alCambiarPagina,
        onSort: alCambiarOrden,
        resetPagination: reiniciarPaginacion
    };
};

export const useTablasPaginadas = usePaginacion;
export default usePaginacion;
