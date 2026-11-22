import { useState, useEffect } from 'react';
import { Tag } from 'primereact/tag';

export default function Temporizador({ fechaInicio, duracionMinutos, alTerminarTiempo }) {
    const [tiempoRestante, setTiempoRestante] = useState('--:--');
    const [esCritico, setEsCritico] = useState(false);

    useEffect(() => {
        if (!fechaInicio || !duracionMinutos) return;

        const tiempoInicio = new Date(fechaInicio).getTime();
        const tiempoFin = tiempoInicio + duracionMinutos * 60 * 1000;

        const intervalo = setInterval(() => {
            const ahora = new Date().getTime();
            const diferencia = tiempoFin - ahora;

            if (diferencia <= 0) {
                clearInterval(intervalo);
                setTiempoRestante('00:00');
                if (alTerminarTiempo) alTerminarTiempo();
            } else {
                const minutos = Math.floor((diferencia % (1000 * 60 * 60)) / (1000 * 60));
                const segundos = Math.floor((diferencia % (1000 * 60)) / 1000);

                setEsCritico(minutos < 5);

                setTiempoRestante(
                    `${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`
                );
            }
        }, 1000);

        return () => clearInterval(intervalo);
    }, [fechaInicio, duracionMinutos, alTerminarTiempo]);

    return (
        <Tag
            severity={esCritico ? 'danger' : 'info'}
            icon="pi pi-clock"
            value={tiempoRestante}
            style={{ fontSize: '1rem', padding: '0.4rem 0.8rem' }}
        />
    );
}
