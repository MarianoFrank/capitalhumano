import { useEffect, useMemo, useState } from 'react';
import { TemaContext } from './TemaContext';

const TEMA_CLARO = 'lara-light-cyan';
const TEMA_OSCURO = 'lara-dark-cyan';

export default function TemaProvider({ children }) {
    const [tema, setTema] = useState(
        () => localStorage.getItem('theme') || TEMA_CLARO
    );

    useEffect(() => {
        const enlaceTema = document.getElementById('theme-link');

        if (!enlaceTema) {
            console.error('No se encontró <link id="theme-link"> en index.html');
            return;
        }

        enlaceTema.href = `/themes/${tema}/theme.css`;
        localStorage.setItem('theme', tema);
    }, [tema]);

    const alternarTema = () => {
        setTema((actual) =>
            actual === TEMA_CLARO ? TEMA_OSCURO : TEMA_CLARO
        );
    };

    const valor = useMemo(
        () => ({
            tema,
            esOscuro: tema === TEMA_OSCURO,
            setTema,
            alternarTema,

            // Alias en inglés
            theme: tema,
            isDark: tema === TEMA_OSCURO,
            setTheme: setTema,
            toggleTheme: alternarTema
        }),
        [tema]
    );

    return (
        <TemaContext.Provider value={valor}>
            {children}
        </TemaContext.Provider>
    );
}

export const ThemeProvider = TemaProvider;
