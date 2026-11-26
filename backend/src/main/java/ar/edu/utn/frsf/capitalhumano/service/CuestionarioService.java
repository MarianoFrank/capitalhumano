package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.CuestionarioDTO;
import ar.edu.utn.frsf.capitalhumano.event.CuestionarioCompletadoEvent;
import ar.edu.utn.frsf.capitalhumano.model.Bloque;
import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.ItemOpcion;
import ar.edu.utn.frsf.capitalhumano.model.ItemPregunta;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CuestionarioService {

    private final CuestionarioRepository cuestionarioRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EvaluacionService evaluacionService;

    @Value("${app.cuestionario.max-accesos:3}")
    private int maxAccesos = 3;

    public CuestionarioService(CuestionarioRepository cuestionarioRepository,
                               ApplicationEventPublisher eventPublisher,
                               EvaluacionService evaluacionService) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.eventPublisher = eventPublisher;
        this.evaluacionService = evaluacionService;
    }

    @Transactional(noRollbackFor = IllegalStateException.class)
    public CuestionarioDTO.Inicio iniciarCuestionario(Long idCuestionario) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        LocalDateTime ahora = LocalDateTime.now();
        validarDisponibilidadYCerrarSiVencio(cuestionario, ahora, true);

        if (cuestionario.getBloques() == null || cuestionario.getBloques().isEmpty()) {
            evaluacionService.generarEstructuraCuestionario(cuestionario, cuestionario.getEvaluacion().getPuesto());
        }

        if (cuestionario.getEstado() == EstadoCuestionario.ACTIVE) {
            cuestionario.setEstado(EstadoCuestionario.IN_PROGRESS);
            cuestionario.setFechaInicio(ahora);
        }

        cuestionario.setCantidadAccesos(cuestionario.getCantidadAccesos() + 1);
        cuestionario.setUltimoAcceso(ahora);

        cuestionarioRepository.save(cuestionario);

        int totalBloques = cuestionario.getBloques().size();
        int bloqueActual = calcularBloqueActual(cuestionario);

        return CuestionarioDTO.Inicio.from(cuestionario, totalBloques, bloqueActual);
    }

    @Transactional(noRollbackFor = IllegalStateException.class)
    public CuestionarioDTO.Bloque obtenerBloque(Long idCuestionario, int numeroBloque) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        validarDisponibilidadYCerrarSiVencio(cuestionario, LocalDateTime.now(), false);

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

        return CuestionarioDTO.Bloque.from(bloque);
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

    @Transactional(noRollbackFor = IllegalStateException.class)
    public void guardarRespuestasBloque(Long idCuestionario, int numeroBloque, Map<Long, List<Long>> respuestas) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        LocalDateTime ahora = LocalDateTime.now();
        validarDisponibilidadYCerrarSiVencio(cuestionario, ahora, false);

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
            cuestionario.setFechaFin(ahora);
            eventPublisher.publishEvent(new CuestionarioCompletadoEvent(cuestionario.getId()));
        }

        cuestionarioRepository.save(cuestionario);
    }

    @Transactional
    public void finalizarPorTiempoAgotado(Long idCuestionario) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        if (cuestionario.getEstado() == EstadoCuestionario.IN_PROGRESS) {
            cuestionario.setEstado(EstadoCuestionario.INCOMPLETE);
            cuestionario.setFechaFin(LocalDateTime.now());
            cuestionarioRepository.save(cuestionario);
        }
    }

    private void validarDisponibilidadYCerrarSiVencio(Cuestionario cuestionario, LocalDateTime ahora, boolean validarMaxAccesos) {
        if (cuestionario.getEstado() == EstadoCuestionario.COMPLETED) {
            throw new IllegalStateException("El cuestionario ya fue completado.");
        }

        if (cuestionario.getEstado() == EstadoCuestionario.INCOMPLETE ||
                cuestionario.getEstado() == EstadoCuestionario.NOT_ANSWERED) {
            throw new IllegalStateException("El cuestionario ya no se encuentra disponible.");
        }

        if (cuestionario.getEvaluacion() != null && cuestionario.getEvaluacion().getFechaCierre() != null
                && ahora.isAfter(cuestionario.getEvaluacion().getFechaCierre())) {
            cuestionario.setEstado(cuestionario.getEstado() == EstadoCuestionario.ACTIVE
                    ? EstadoCuestionario.NOT_ANSWERED
                    : EstadoCuestionario.INCOMPLETE);
            cuestionario.setFechaFin(ahora);
            cuestionarioRepository.save(cuestionario);
            throw new IllegalStateException("El plazo para realizar el cuestionario ha vencido.");
        }

        if (cuestionario.getEstado() == EstadoCuestionario.IN_PROGRESS && cuestionario.getFechaInicio() != null
                && cuestionario.getEvaluacion() != null && cuestionario.getEvaluacion().getDuracion() != null) {
            LocalDateTime limite = cuestionario.getFechaInicio().plusMinutes(cuestionario.getEvaluacion().getDuracion());
            if (ahora.isAfter(limite)) {
                cuestionario.setEstado(EstadoCuestionario.INCOMPLETE);
                cuestionario.setFechaFin(ahora);
                cuestionarioRepository.save(cuestionario);
                throw new IllegalStateException("Tiempo para completar el cuestionario agotado.");
            }
        }

        if (validarMaxAccesos && cuestionario.getCantidadAccesos() + 1 > maxAccesos) {
            cuestionario.setEstado(EstadoCuestionario.INCOMPLETE);
            cuestionario.setFechaFin(ahora);
            cuestionarioRepository.save(cuestionario);
            throw new IllegalStateException(
                    "Superó la cantidad máxima de accesos permitidos. El cuestionario quedó en estado Incompleto.");
        }
    }

    @Transactional
    public void finalizarCuestionariosVencidos() {
        LocalDateTime ahora = LocalDateTime.now();

        List<EstadoCuestionario> estadosAbiertos = List.of(
                EstadoCuestionario.ACTIVE,
                EstadoCuestionario.IN_PROGRESS);

        List<Cuestionario> abiertos = cuestionarioRepository.findByEstadoIn(estadosAbiertos);
        List<Cuestionario> cuestionariosVencidos = new ArrayList<>();

        for (Cuestionario q : abiertos) {
            boolean vencioCierre = q.getEvaluacion() != null && q.getEvaluacion().getFechaCierre() != null
                    && q.getEvaluacion().getFechaCierre().isBefore(ahora);
            boolean vencioDuracion = q.getEstado() == EstadoCuestionario.IN_PROGRESS
                    && q.getFechaInicio() != null
                    && q.getEvaluacion() != null && q.getEvaluacion().getDuracion() != null
                    && q.getFechaInicio().plusMinutes(q.getEvaluacion().getDuracion()).isBefore(ahora);

            if (vencioCierre || vencioDuracion) {
                q.setEstado(q.getEstado() == EstadoCuestionario.ACTIVE
                        ? EstadoCuestionario.NOT_ANSWERED
                        : EstadoCuestionario.INCOMPLETE);
                q.setFechaFin(ahora);
                cuestionariosVencidos.add(q);
            }
        }

        if (!cuestionariosVencidos.isEmpty()) {
            cuestionarioRepository.saveAll(cuestionariosVencidos);
        }
    }
}
