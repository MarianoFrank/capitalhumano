package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.dto.PuestoDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.CuestionarioMapper;
import ar.edu.utn.frsf.capitalhumano.mapper.EvaluacionMapper;
import ar.edu.utn.frsf.capitalhumano.mapper.PuestoMapper;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import ar.edu.utn.frsf.capitalhumano.repository.EvaluacionRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PuestoRepository;
import ar.edu.utn.frsf.capitalhumano.specification.PuestoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    private final CuestionarioRepository cuestionarioRepository;
    private final PuestoRepository puestoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final PuntajeAsyncService puntajeAsyncService;
    private final PuestoMapper puestoMapper;
    private final CuestionarioMapper cuestionarioMapper;
    private final EvaluacionMapper evaluacionMapper;

    public ReporteService(CuestionarioRepository cuestionarioRepository,
            PuestoRepository puestoRepository,
            EvaluacionRepository evaluacionRepository,
            PuntajeAsyncService puntajeAsyncService,
            PuestoMapper puestoMapper,
            CuestionarioMapper cuestionarioMapper,
            EvaluacionMapper evaluacionMapper) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.puestoRepository = puestoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.puntajeAsyncService = puntajeAsyncService;
        this.puestoMapper = puestoMapper;
        this.cuestionarioMapper = cuestionarioMapper;
        this.evaluacionMapper = evaluacionMapper;
    }

    @Transactional(readOnly = true)
    public EvaluacionDTO.ReporteOrdenMerito generarOrdenMerito(Long idPuesto, Long idEvaluacion) {
        Puesto puesto = puestoRepository.findById(idPuesto)
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado con ID: " + idPuesto));

        List<Cuestionario> cuestionarios;
        if (idEvaluacion != null) {
            cuestionarios = cuestionarioRepository.findByEvaluacionId(idEvaluacion);
        } else {
            cuestionarios = cuestionarioRepository.findByEvaluacionPuestoId(idPuesto);
        }

        List<EvaluacionDTO.ReporteCandidato> aprobados = new ArrayList<>();
        List<EvaluacionDTO.ReporteCandidato> noAprobados = new ArrayList<>();

        for (Cuestionario q : cuestionarios) {
            if (q.getEstado() == EstadoCuestionario.COMPLETED &&
                    (q.getPuntajeTotal() == null || q.getPuntajesCompetencias() == null
                            || q.getPuntajesCompetencias().isEmpty())) {
                puntajeAsyncService.calcularPuntajeSincrono(q.getId());
                q = cuestionarioRepository.findById(q.getId()).orElse(q);
            }

            EvaluacionDTO.ReporteCandidato dto = cuestionarioMapper.aReporteCandidato(q);

            if (esCandidatoAprobado(q, puesto)) {
                aprobados.add(dto);
            } else {
                noAprobados.add(dto);
            }
        }

        aprobados.sort(Comparator.comparing(EvaluacionDTO.ReporteCandidato::puntaje,
                Comparator.nullsLast(Comparator.reverseOrder())));
        noAprobados.sort(Comparator.comparing(EvaluacionDTO.ReporteCandidato::puntaje,
                Comparator.nullsLast(Comparator.reverseOrder())));

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String nombreConsultor = (auth != null && auth.getName() != null) ? auth.getName() : "Sistema";

        return cuestionarioMapper.aReporteOrdenMerito(
                puesto,
                nombreConsultor,
                LocalDateTime.now(),
                aprobados,
                noAprobados);
    }

    private boolean esCandidatoAprobado(Cuestionario cuestionario, Puesto puesto) {
        if (cuestionario.getEstado() != EstadoCuestionario.COMPLETED) {
            return false;
        }

        List<PuestoCompetencia> competenciasPuesto = puesto.getCompetencias();
        if (competenciasPuesto == null || competenciasPuesto.isEmpty()) {
            return true;
        }

        List<PuntajeCompetencia> puntajesCompetencias = cuestionario.getPuntajesCompetencias();
        if (puntajesCompetencias == null || puntajesCompetencias.isEmpty()) {
            return false;
        }

        for (PuestoCompetencia pc : competenciasPuesto) {
            Competencia compRequerida = pc.getCompetencia();
            if (compRequerida == null || compRequerida.getFechaBaja() != null) {
                continue;
            }

            Integer ponderacionRequerida = pc.getPonderacionRequerida();
            if (ponderacionRequerida == null) {
                continue;
            }

            // Buscamos el puntaje obtenido por el candidato en esta competencia específica
            Optional<PuntajeCompetencia> puntajeObtenido = puntajesCompetencias.stream()
                    .filter(pcScore -> pcScore.getCompetencia() != null &&
                            pcScore.getCompetencia().getId().equals(compRequerida.getId()))
                    .findFirst();

            if (puntajeObtenido.isEmpty()) {
                return false;
            }

            Double score = puntajeObtenido.get().getPuntaje();
            if (score == null || score < ponderacionRequerida.doubleValue()) {
                return false;
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public Page<PuestoDTO.Resumen> obtenerPuestosParaReporte(
            Long idEmpresa, String nombrePuesto, String codigo, Pageable pageable) {

        Specification<Puesto> spec = PuestoSpecification.conFiltrosReporte(idEmpresa, nombrePuesto, codigo);
        Page<Puesto> puestosPage = puestoRepository.findAll(spec, pageable);

        List<PuestoDTO.Resumen> listaDto = new ArrayList<>();
        for (Puesto puesto : puestosPage.getContent()) {
            List<Cuestionario> cuestionarios = cuestionarioRepository.findByEvaluacionPuestoId(puesto.getId());
            int totalCandidatos = cuestionarios.size();
            int completadas = (int) cuestionarios.stream()
                    .filter(c -> c.getEstado() == EstadoCuestionario.COMPLETED)
                    .count();

            listaDto.add(puestoMapper.aResumen(puesto, totalCandidatos, completadas));
        }

        return new PageImpl<>(listaDto, pageable, puestosPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO.Resumen> obtenerResumenEvaluacionesPorPuesto(Long idPuesto) {
        List<Evaluacion> evaluaciones = evaluacionRepository.findByPuestoId(idPuesto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<EvaluacionDTO.Resumen> lista = new ArrayList<>();
        for (Evaluacion ev : evaluaciones) {
            List<Cuestionario> cuestionarios = cuestionarioRepository.findByEvaluacionId(ev.getId());
            int total = cuestionarios.size();
            long completadas = cuestionarios.stream().filter(c -> c.getEstado() == EstadoCuestionario.COMPLETED)
                    .count();

            String fechaStr = ev.getFechaCreacion() != null ? ev.getFechaCreacion().format(formatter) : "Sin fecha";
            String descripcion = String.format("%s - Candidatos: %d - Completadas: %d", fechaStr, total, completadas);

            lista.add(evaluacionMapper.aResumen(ev.getId(), descripcion));
        }
        return lista;
    }
}
