import { createContext, useContext, useState, useEffect, useCallback, useMemo } from 'react';

export const TemaContext = createContext(null);

const TEMA_CLARO = 'lara-light-cyan';
const TEMA_OSCURO = 'lara-dark-cyan';

export function ProveedorTema({ children }) {
    const [tema, setTema] = useState(
        () => localStorage.getItem('tema') || localStorage.getItem('theme') || TEMA_CLARO
    );

    useEffect(() => {
        const enlaceTema = document.getElementById('theme-link');
        if (!enlaceTema) {
            console.error('No se encontró <link id="theme-link"> en index.html');
            return;
        }

        enlaceTema.href = `/themes/${tema}/theme.css`;
        localStorage.setItem('tema', tema);
    }, [tema]);

    const alternarTema = useCallback(() => {
        setTema((actual) => (actual === TEMA_CLARO ? TEMA_OSCURO : TEMA_CLARO));
    }, []);

    const valor = useMemo(() => ({
        tema,
        esOscuro: tema === TEMA_OSCURO,
        setTema,
        alternarTema,
        // Alias de compatibilidad
        theme: tema,
        isDark: tema === TEMA_OSCURO,
        toggleTheme: alternarTema
    }), [tema, alternarTema]);

    return (
        <TemaContext.Provider value={valor}>
            {children}
        </TemaContext.Provider>
    );
}

export const useTema = () => {
    const context = useContext(TemaContext);
    if (!context) {
        throw new Error('useTema debe usarse dentro de un ProveedorTema');
    }
    return context;
};

export default TemaContext;
