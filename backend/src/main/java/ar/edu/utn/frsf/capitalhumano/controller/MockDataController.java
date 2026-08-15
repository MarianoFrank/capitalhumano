package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.repository.EvaluacionRepository;
import ar.edu.utn.frsf.capitalhumano.model.Bloque;
import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.Evaluacion;
import ar.edu.utn.frsf.capitalhumano.model.ItemOpcion;
import ar.edu.utn.frsf.capitalhumano.model.ItemPregunta;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import ar.edu.utn.frsf.capitalhumano.service.CuestionarioService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/simulacion")
public class MockDataController {

    private final CuestionarioRepository cuestionarioRepository;
    private final CuestionarioService cuestionarioService;
    private final EvaluacionRepository evaluacionRepository;

    public MockDataController(CuestionarioRepository cuestionarioRepository,
            CuestionarioService cuestionarioService,
            EvaluacionRepository evaluacionRepository) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.cuestionarioService = cuestionarioService;
        this.evaluacionRepository = evaluacionRepository;
    }

    // Responde automáticamente al azar a todos los cuestionarios de una evaluación,
    // simulando que algunos candidatos lo completan, otros lo dejan a medias y otros ni entran.
    @PostMapping("/responder-todo/{idEvaluacion}")
    @Transactional
    public String responderTodoAleatorio(@PathVariable Long idEvaluacion) {

        // 1. Buscamos la evaluación para poder cerrarla al final
        Evaluacion evaluacion = evaluacionRepository.findById(idEvaluacion)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        List<Cuestionario> cuestionarios = cuestionarioRepository.findByEvaluacionId(idEvaluacion);
        Random random = new Random();

        int countCompletos = 0;
        int countIncompletos = 0;
        int countIgnorados = 0;

        // --- INICIA LA SIMULACIÓN DE CANDIDATOS ---
        for (Cuestionario q : cuestionarios) {
            if (q.getEstado() == EstadoCuestionario.ACTIVE) {

                int decision = random.nextInt(100);

                // CASO 1: 20% ni entran al cuestionario
                if (decision < 20) {
                    countIgnorados++;
                    continue;
                }

                cuestionarioService.iniciarCuestionario(q.getId());
                Cuestionario updatedQ = cuestionarioRepository.findById(q.getId()).orElseThrow();
                List<Bloque> bloquesCopia = new ArrayList<>(updatedQ.getBloques());

                int bloquesACompletar = bloquesCopia.size();

                // CASO 2: 30% lo deja por la mitad
                if (decision >= 20 && decision < 50 && bloquesCopia.size() > 1) {
                    bloquesACompletar = random.nextInt(bloquesCopia.size() - 1) + 1;
                    countIncompletos++;
                } else {
                    // CASO 3: 50% lo hace completo
                    countCompletos++;
                }

                for (int i = 0; i < bloquesACompletar; i++) {
                    Bloque block = bloquesCopia.get(i);
                    Map<Long, List<Long>> answers = new HashMap<>();
                    List<ItemPregunta> itemsCopia = new ArrayList<>(block.getItemsPregunta());

                    for (ItemPregunta item : itemsCopia) {
                        List<ItemOpcion> options = new ArrayList<>(item.getItemsOpcion());
                        List<Long> selectedIds = new ArrayList<>();

                        boolean isMultiple = item.getPregunta().getTipo() != null &&
                                item.getPregunta().getTipo().name().toUpperCase().contains("MULTIPLE");

                        if (isMultiple) {
                            // Seleccionamos al azar entre 1 y todas las opciones disponibles
                            int cantOpciones = random.nextInt(options.size()) + 1;
                            Collections.shuffle(options);
                            for (int j = 0; j < cantOpciones; j++) {
                                selectedIds.add(options.get(j).getId());
                            }
                        } else {
                            ItemOpcion randomOption = options.get(random.nextInt(options.size()));
                            selectedIds.add(randomOption.getId());
                        }

                        answers.put(item.getId(), selectedIds);
                    }
                    // Enviamos las respuestas del bloque al servicio para que las procese
                    cuestionarioService.guardarRespuestasBloque(updatedQ.getId(), block.getNumeroBloque(), answers);
                }
            }
        }
        // --- FIN DE LA SIMULACIÓN DE CANDIDATOS ---

        // Vencemos la evaluación poniéndola en tiempo pasado
        evaluacion.setFechaCierre(LocalDateTime.now().minusDays(1));
        evaluacionRepository.save(evaluacion);

        // Forzamos la ejecución de la limpieza de vencidos
        cuestionarioService.finalizarCuestionariosVencidos();

        return String.format(
                "¡Flujo completo simulado con éxito! \n" +
                        "Ruleta: %d Completos, %d Incompletos, %d Ignorados. \n" +
                        "La evaluación se cerró (fecha modificada al pasado) y los cuestionarios pendientes fueron actualizados.",
                countCompletos, countIncompletos, countIgnorados);
    }
}
