package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.CuestionarioDTO;
import ar.edu.utn.frsf.capitalhumano.event.CuestionarioCompletadoEvent;
import ar.edu.utn.frsf.capitalhumano.mapper.CuestionarioMapper;
import ar.edu.utn.frsf.capitalhumano.model.Bloque;
import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.ItemOpcion;
import ar.edu.utn.frsf.capitalhumano.model.ItemPregunta;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CuestionarioService {

    private final CuestionarioRepository cuestionarioRepository;
    private final CuestionarioMapper cuestionarioMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final EvaluacionService evaluacionService;

    public CuestionarioService(CuestionarioRepository cuestionarioRepository,
                               CuestionarioMapper cuestionarioMapper,
                               ApplicationEventPublisher eventPublisher,
                               EvaluacionService evaluacionService) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.cuestionarioMapper = cuestionarioMapper;
        this.eventPublisher = eventPublisher;
        this.evaluacionService = evaluacionService;
    }

    @Transactional
    public CuestionarioDTO.Inicio iniciarCuestionario(Long idCuestionario) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        if (cuestionario.getEstado() == EstadoCuestionario.COMPLETED) {
            throw new IllegalArgumentException("El cuestionario ya fue completado.");
        }

        if (cuestionario.getBloques() == null || cuestionario.getBloques().isEmpty()) {
            evaluacionService.generarEstructuraCuestionario(cuestionario, cuestionario.getEvaluacion().getPuesto());
        }

        if (cuestionario.getEstado() == EstadoCuestionario.ACTIVE) {
            cuestionario.setEstado(EstadoCuestionario.IN_PROGRESS);
            cuestionario.setFechaInicio(LocalDateTime.now());
        }

        cuestionario.setCantidadAccesos(cuestionario.getCantidadAccesos() + 1);
        cuestionario.setUltimoAcceso(LocalDateTime.now());

        cuestionarioRepository.save(cuestionario);

        int totalBloques = cuestionario.getBloques().size();
        int bloqueActual = calcularBloqueActual(cuestionario);

        return cuestionarioMapper.aInicio(cuestionario, totalBloques, bloqueActual);
    }

    @Transactional
    public CuestionarioDTO.Bloque obtenerBloque(Long idCuestionario, int numeroBloque) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        if (cuestionario.getEstado() != EstadoCuestionario.IN_PROGRESS) {
            throw new IllegalArgumentException("El cuestionario no se encuentra en curso.");
        }

        if (cuestionario.getBloques() == null || cuestionario.getBloques().isEmpty()) {
            evaluacionService.generarEstructuraCuestionario(cuestionario, cuestionario.getEvaluacion().getPuesto());
            cuestionarioRepository.save(cuestionario);
        }

        int bloquePermitido = calcularBloqueActual(cuestionario);
        if (numeroBloque != bloquePermitido) {
            throw new IllegalArgumentException(
                    "No puedes acceder a este bloque. Debes completar los bloques en orden secuencial.");
        }

        Bloque bloque = cuestionario.getBloques().stream()
                .filter(b -> b.getNumeroBloque() == numeroBloque)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bloque no encontrado."));

        return cuestionarioMapper.aBloque(bloque);
    }

    private int calcularBloqueActual(Cuestionario cuestionario) {
        if (cuestionario.getBloques() == null || cuestionario.getBloques().isEmpty()) {
            return 1;
        }
        for (Bloque b : cuestionario.getBloques()) {
            boolean bloqueCompleto = true;
            if (b.getItemsPregunta() != null) {
                for (ItemPregunta item : b.getItemsPregunta()) {
                    boolean respondida = item.getItemsOpcion() != null && item.getItemsOpcion().stream()
                            .anyMatch(opt -> Boolean.TRUE.equals(opt.getEstaRespondida()));
                    if (!respondida) {
                        bloqueCompleto = false;
                        break;
                    }
                }
            }
            if (!bloqueCompleto) {
                return b.getNumeroBloque();
            }
        }
        return Math.max(1, cuestionario.getBloques().size());
    }

    @Transactional
    public void guardarRespuestasBloque(Long idCuestionario, int numeroBloque, Map<Long, List<Long>> respuestas) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        if (cuestionario.getEstado() != EstadoCuestionario.IN_PROGRESS) {
            throw new IllegalArgumentException("El cuestionario ya fue finalizado o no está en curso.");
        }

        int bloquePermitido = calcularBloqueActual(cuestionario);
        if (numeroBloque != bloquePermitido) {
            throw new IllegalArgumentException(
                    "No puedes enviar respuestas para este bloque. Debes completar los bloques en orden.");
        }

        Bloque block = cuestionario.getBloques().stream()
                .filter(b -> b.getNumeroBloque() == numeroBloque)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bloque no encontrado."));

        for (ItemPregunta item : block.getItemsPregunta()) {
            List<Long> selectedOptionIds = respuestas.get(item.getId());

            if (selectedOptionIds == null) {
                throw new IllegalArgumentException("Falta responder preguntas del bloque.");
            }

            boolean isMultiple = item.getPregunta().getTipo().name().toUpperCase().contains("MULTIPLE");

            if (!isMultiple && selectedOptionIds.size() > 1) {
                throw new IllegalArgumentException(
                        "No podés seleccionar más de una opción en una pregunta de selección única.");
            }

            for (ItemOpcion optItem : item.getItemsOpcion()) {
                if (selectedOptionIds.contains(optItem.getId())) {
                    optItem.setEstaRespondida(true);
                } else {
                    optItem.setEstaRespondida(false);
                }
            }
        }

        if (numeroBloque == cuestionario.getBloques().size()) {
            cuestionario.setEstado(EstadoCuestionario.COMPLETED);
            cuestionario.setFechaFin(LocalDateTime.now());
            eventPublisher.publishEvent(new CuestionarioCompletadoEvent(cuestionario.getId()));
        }

        cuestionarioRepository.save(cuestionario);
    }

    @Transactional
    public void finalizarCuestionariosVencidos() {
        LocalDateTime ahora = LocalDateTime.now();

        List<EstadoCuestionario> estadosAbiertos = List.of(
                EstadoCuestionario.ACTIVE,
                EstadoCuestionario.IN_PROGRESS);

        List<Cuestionario> cuestionariosVencidos = cuestionarioRepository
                .findExpiredQuestionnaires(ahora, estadosAbiertos);

        if (cuestionariosVencidos.isEmpty()) {
            return;
        }

        for (Cuestionario q : cuestionariosVencidos) {
            if (q.getEstado() == EstadoCuestionario.ACTIVE) {
                q.setEstado(EstadoCuestionario.NOT_ANSWERED);
            } else if (q.getEstado() == EstadoCuestionario.IN_PROGRESS) {
                q.setEstado(EstadoCuestionario.INCOMPLETE);
            }
            q.setFechaFin(ahora);
        }

        cuestionarioRepository.saveAll(cuestionariosVencidos);
    }
}
