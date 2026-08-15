import { useState, useEffect } from 'react';
import { Tag } from 'primereact/tag';

export default function Temporizador({ fechaInicio, duracionMinutos, alTerminarTiempo, startedAt, durationMinutes, onTimeUp }) {
    const inicio = fechaInicio || startedAt;
    const duracion = duracionMinutos || durationMinutes;
    const alTerminar = alTerminarTiempo || onTimeUp;

    const [tiempoRestante, setTiempoRestante] = useState('--:--');
    const [esCritico, setEsCritico] = useState(false);

    useEffect(() => {
        if (!inicio || !duracion) return;

        const tiempoInicio = new Date(inicio).getTime();
        const tiempoFin = tiempoInicio + duracion * 60 * 1000;

        const intervalo = setInterval(() => {
            const ahora = new Date().getTime();
            const diferencia = tiempoFin - ahora;

            if (diferencia <= 0) {
                clearInterval(intervalo);
                setTiempoRestante('00:00');
                if (alTerminar) alTerminar();
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
    }, [inicio, duracion, alTerminar]);

    return (
        <Tag
            severity={esCritico ? 'danger' : 'info'}
            icon="pi pi-clock"
            value={tiempoRestante}
            style={{ fontSize: '1rem', padding: '0.4rem 0.8rem' }}
        />
    );
}

export const Timer = Temporizador;
