package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.model.cuestionario.*;
import ar.edu.utn.frsf.capitalhumano.event.CuestionarioCompletadoEvent;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;

import java.util.ArrayList;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class PuntajeAsyncService {

    private final CuestionarioRepository cuestionarioRepository;

    public PuntajeAsyncService(CuestionarioRepository cuestionarioRepository) {
        this.cuestionarioRepository = cuestionarioRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void calcularPuntajesAsync(CuestionarioCompletadoEvent event) {
        calcularPuntajeSincrono(event.idCuestionario());
    }

    @Transactional
    public Double calcularPuntajeSincrono(Long idCuestionario) {

        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado para cálculo"));

        // Aseguramos que la lista no sea nula antes de limpiarla
        if (cuestionario.getPuntajesCompetencias() != null) {
            cuestionario.getPuntajesCompetencias().clear();
        } else {
            cuestionario.setPuntajesCompetencias(new ArrayList<>());
        }

        int cantidadCompetencias = cuestionario.getEvaluacion().getPuesto().getCompetencias().size();
        double sumaTotalesCompetencias = 0.0;

        for (PuestoCompetencia pc : cuestionario.getEvaluacion().getPuesto().getCompetencias()) {
            Competencia competency = pc.getCompetencia();

            PuntajeCompetencia compScore = new PuntajeCompetencia();
            compScore.setCuestionario(cuestionario);
            compScore.setCompetencia(competency);
            compScore.setPuntajesFactores(new ArrayList<>());

            double sumaTotalesFactores = 0.0;
            int cantidadFactoresDeEstaCompetencia = competency.getFactores().size();

            for (Factor factor : competency.getFactores()) {
                boolean factorFueEvaluado = false;
                double sumaPesosFactor = 0;

                for (Bloque block : cuestionario.getBloques()) {
                    for (ItemPregunta item : block.getItemsPregunta()) {
                        if (item.getPregunta().getFactor().getId().equals(factor.getId())) {
                            factorFueEvaluado = true;

                            double sumaPesosPregunta = 0.0;
                            double penalizacion = 0.0;

                            boolean isMultiple = item.getPregunta().getTipo() != null &&
                                    item.getPregunta().getTipo().name().toUpperCase().contains("MULTIPLE");

                            // Valor que se restará por cada opción distractora seleccionada
                            double valorPenalizacion = 0.0;
                            if (!item.getItemsOpcion().isEmpty()) {
                                valorPenalizacion = 10.0 / item.getItemsOpcion().size();
                            }

                            for (ItemOpcion optItem : item.getItemsOpcion()) {
                                if (Boolean.TRUE.equals(optItem.getEstaRespondida())) {
                                    double peso = optItem.getOpcion().getPonderacion();
                                    sumaPesosPregunta += peso;

                                    if (isMultiple && peso == 0.0) {
                                        penalizacion += valorPenalizacion;
                                    }
                                }
                            }

                            // Aplicamos la penalización asegurando que el puntaje no baje de 0
                            double puntajeFinalPregunta = Math.max(0.0, sumaPesosPregunta - penalizacion);
                            sumaPesosFactor += puntajeFinalPregunta;
                        }
                    }
                }

                if (factorFueEvaluado) {
                    // el puntaje del factor es la suma de los pesos de sus preguntas dividido por 2
                    double puntajeFactor = sumaPesosFactor / 2.0;

                    PuntajeFactor fScore = new PuntajeFactor();
                    fScore.setPuntajeCompetencia(compScore);
                    fScore.setFactor(factor);
                    fScore.setPuntaje(puntajeFactor);

                    compScore.getPuntajesFactores().add(fScore);
                    sumaTotalesFactores += puntajeFactor;
                }
            }

            double puntajeCompetencia = 0.0;
            if (cantidadFactoresDeEstaCompetencia > 0) {
                puntajeCompetencia = sumaTotalesFactores / cantidadFactoresDeEstaCompetencia;
            }

            compScore.setPuntaje(puntajeCompetencia);
            cuestionario.getPuntajesCompetencias().add(compScore);
            sumaTotalesCompetencias += puntajeCompetencia;
        }

        double puntajeTotalCuestionario = 0.0;
        if (cantidadCompetencias > 0) {
            puntajeTotalCuestionario = sumaTotalesCompetencias / cantidadCompetencias;
        }

        cuestionario.setPuntajeTotal(puntajeTotalCuestionario);

        cuestionarioRepository.save(cuestionario);

        return puntajeTotalCuestionario;
    }
}
