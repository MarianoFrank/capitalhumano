package ar.edu.utn.frsf.capitalhumano.scheduler;

import ar.edu.utn.frsf.capitalhumano.service.CuestionarioService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CuestionarioVencimientoScheduler {

    private final CuestionarioService cuestionarioService;

    public CuestionarioVencimientoScheduler(CuestionarioService cuestionarioService) {
        this.cuestionarioService = cuestionarioService;
    }

    /**
     * Se ejecuta todos los días a las 00:00 hs.
     * Cron expression: "Segundos Minutos Horas Día_del_mes Mes Día_de_la_semana"
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void procesarCuestionariosVencidos() {
        System.out.println("Iniciando job nocturno: finalización de cuestionarios vencidos...");
        cuestionarioService.finalizarCuestionariosVencidos();
    }
}
