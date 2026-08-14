package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.response.ReporteCandidatoResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.EvaluacionResumenResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.ReporteOrdenMeritoResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PuestoResumenResponse;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.cuestionario.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.cuestionario.PuntajeCompetencia;
import ar.edu.utn.frsf.capitalhumano.repository.EvaluacionRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PuestoRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReporteService {

    private final CuestionarioRepository cuestionarioRepository;
    private final PuestoRepository puestoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final PuntajeAsyncService puntajeAsyncService;

    public ReporteService(CuestionarioRepository cuestionarioRepository, PuestoRepository puestoRepository,
            EvaluacionRepository evaluacionRepository, PuntajeAsyncService puntajeAsyncService) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.puestoRepository = puestoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.puntajeAsyncService = puntajeAsyncService;
    }

    @Transactional(readOnly = true)
    public ReporteOrdenMeritoResponse generarOrdenMerito(Long idPuesto, Long idEvaluacion) {
        Puesto puesto = puestoRepository.findById(idPuesto)
                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));

        List<Cuestionario> cuestionarios;

        // Validamos que la evaluación exista y que pertenezca al puesto
        if (idEvaluacion != null) {
            Evaluacion evaluacion = evaluacionRepository.findById(idEvaluacion)
                    .orElseThrow(() -> new IllegalArgumentException("La evaluación especificada no existe"));

            if (!evaluacion.getPosition().getId().equals(idPuesto)) {
                throw new IllegalArgumentException("La evaluación enviada no pertenece al puesto seleccionado");
            }

            cuestionarios = cuestionarioRepository.findByEvaluationId(idEvaluacion);
        } else {
            cuestionarios = cuestionarioRepository.findByEvaluationPositionId(idPuesto);
        }

        List<ReporteCandidatoResponse> aprobados = new ArrayList<>();
        List<ReporteCandidatoResponse> noAprobados = new ArrayList<>();

        for (Cuestionario q : cuestionarios) {
            // Si está completado pero el servidor se cayó antes de calcular el puntaje
            if (q.getEstado() == EstadoCuestionario.COMPLETED && q.getPuntajeTotal() == null) {
                // Lo calculamos sincrónicamente
                Double puntajeRecuperado = puntajeAsyncService.calcularPuntajeSincrono(q.getId());
                q.setPuntajeTotal(puntajeRecuperado);
            }

            Candidato c = q.getCandidato();

            // Mapeamos los datos adicionales según si completó o no
            LocalDateTime fechaFinal = (q.getEstado() == EstadoCuestionario.COMPLETED) ? q.getFechaFin()
                    : q.getUltimoAcceso();

            ReporteCandidatoResponse candidatoDto = new ReporteCandidatoResponse(
                    c.getNombre(),
                    c.getApellido(),
                    c.getTipoDocumento().name(),
                    c.getNumeroDocumento(),
                    String.valueOf(c.getNumeroCandidato()),
                    q.getEstado().name(),
                    (q.getEstado() == EstadoCuestionario.COMPLETED) ? q.getPuntajeTotal() : null,
                    q.getFechaInicio(),
                    fechaFinal,
                    q.getCantidadAccesos());

            if (q.getEstado() == EstadoCuestionario.COMPLETED && cumpleRequisitosMinimos(q, puesto)) {
                aprobados.add(candidatoDto);
            } else {
                noAprobados.add(candidatoDto);
            }
        }

        aprobados.sort(Comparator.comparing(ReporteCandidatoResponse::score, Comparator.reverseOrder()));
        noAprobados.sort(Comparator.comparing(ReporteCandidatoResponse::state));

        // Sacamos el nombre del consultor que lo está emitiendo
        String consultor = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ReporteOrdenMeritoResponse(
                puesto.getEmpresa().getNombre(),
                puesto.getNombre(),
                consultor,
                LocalDateTime.now(),
                aprobados,
                noAprobados);
    }

    // Valida si el candidato alcanzó las ponderaciones mínimas en TODAS las competencias
    private boolean cumpleRequisitosMinimos(Cuestionario q, Puesto p) {
        for (PuestoCompetencia pc : p.getCompetencias()) {
            Long compId = pc.getCompetencia().getId();

            double minRequired = pc.getPonderacionRequerida();

            // Buscamos cuánto sacó el candidato en esta competencia específica
            double candidateScore = q.getPuntajesCompetencias().stream()
                    .filter(cs -> cs.getCompetencia().getId().equals(compId))
                    .map(PuntajeCompetencia::getPuntaje)
                    .findFirst()
                    .orElse(0.0);

            if (candidateScore < minRequired) {
                return false; // No alcanzó el mínimo requerido
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<PuestoResumenResponse> obtenerPuestosParaReporte(Long idEmpresa, String nombrePuesto, String codigo,
            Pageable pageable) {

        Page<Puesto> positions = puestoRepository.findWithFilters(idEmpresa, nombrePuesto, codigo, pageable);

        return positions.map(pos -> {
            // Traemos todos los cuestionarios de este puesto
            List<Cuestionario> qs = cuestionarioRepository.findByEvaluationPositionId(pos.getId());

            int totalCandidates = qs.size();
            int completed = (int) qs.stream().filter(q -> q.getEstado() == EstadoCuestionario.COMPLETED).count();

            return new PuestoResumenResponse(
                    pos.getId(),
                    pos.getCodigo(),
                    pos.getNombre(),
                    pos.getEmpresa().getNombre(),
                    totalCandidates,
                    completed);
        });
    }

    @Transactional(readOnly = true)
    public List<EvaluacionResumenResponse> obtenerResumenEvaluacionesPorPuesto(Long idPuesto) {

        List<Evaluacion> evaluations = evaluacionRepository.findByPositionId(idPuesto);
        List<EvaluacionResumenResponse> dtoList = new ArrayList<>();

        for (Evaluacion eval : evaluations) {
            // Traemos los cuestionarios solo de esta evaluación
            List<Cuestionario> qs = cuestionarioRepository.findByEvaluationId(eval.getId());

            int total = qs.size();
            int completados = (int) qs.stream().filter(q -> q.getEstado() == EstadoCuestionario.COMPLETED).count();

            // Formateamos la fecha
            String fechaApertura = eval.getFechaCreacion() != null
                    ? eval.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                    : "N/A";

            String description = String.format("%s - Candidatos:%d - Evaluaciones Completadas:%d",
                    fechaApertura, total, completados);

            dtoList.add(new EvaluacionResumenResponse(eval.getId(), description));
        }

        return dtoList;
    }
}
