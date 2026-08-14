package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.request.GenerarEvaluacionRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.ClaveEvaluacionResponse;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.cuestionario.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EvaluacionService {

    private final PuestoRepository puestoRepository;
    private final CandidatoRepository candidatoRepository;
    private final CompetenciaRepository competenciaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final CuestionarioRepository cuestionarioRepository;
    private final ConsultorRepository consultorRepository;

    // Constantes para la clave aleatoria
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CLAVE = 8;
    private final SecureRandom random = new SecureRandom();

    public EvaluacionService(PuestoRepository puestoRepository, CandidatoRepository candidatoRepository,
            CompetenciaRepository competenciaRepository, EvaluacionRepository evaluacionRepository,
            CuestionarioRepository cuestionarioRepository, ConsultorRepository consultorRepository) {
        this.puestoRepository = puestoRepository;
        this.candidatoRepository = candidatoRepository;
        this.competenciaRepository = competenciaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.cuestionarioRepository = cuestionarioRepository;
        this.consultorRepository = consultorRepository;
    }

    @Transactional
    public List<ClaveEvaluacionResponse> generarEvaluacion(GenerarEvaluacionRequest peticion) {

        Puesto puesto = puestoRepository.findById(peticion.idPuesto())
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));

        // validamos si las competencias del puesto cumplen con los requisitos para ser evaluadas
        List<Long> compIdsValidos = competenciaRepository.findValidCompetencyIds();
        for (PuestoCompetencia pc : puesto.getCompetencias()) {
            if (!compIdsValidos.contains(pc.getCompetencia().getId())) {
                throw new RuntimeException("Error: La competencia " + pc.getCompetencia().getNombre()
                        + " no cumple los requisitos para ser evaluada.");
            }
        }

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setPuesto(puesto);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Consultor consultor = consultorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en la base de datos"));

        evaluacion.setConsultor(consultor);
        evaluacion.setCodigo("EVAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        // Le sumamos 7 días pero clavamos la hora a las 23:59:59 para sincronizar con el scheduler
        evaluacion.setFechaCierre(LocalDateTime.now().plusDays(7).with(LocalTime.MAX));
        evaluacion.setDuracion(60); // 60 minutos de tiempo para el test

        evaluacion = evaluacionRepository.save(evaluacion);

        // 4. Buscar candidatos y preparar los cuestionarios
        List<Candidato> candidatos = candidatoRepository.findAllById(peticion.idsCandidatos());
        List<Cuestionario> cuestionarios = new ArrayList<>();
        List<ClaveEvaluacionResponse> listaRespuesta = new ArrayList<>();

        for (Candidato candidato : candidatos) {
            Cuestionario q = new Cuestionario();
            q.setEvaluacion(evaluacion);
            q.setCandidato(candidato);

            // Generar clave de 8 caracteres alfanuméricos
            String claveAcceso = generarClaveAcceso();
            q.setClaveAcceso(claveAcceso);

            q.setCantidadAccesos(0);
            q.setEstado(EstadoCuestionario.ACTIVE);

            cuestionarios.add(q);

            // Llenamos la lista para devolverle al Frontend y que arme el Excel
            listaRespuesta.add(new ClaveEvaluacionResponse(
                    String.valueOf(candidato.getNumeroCandidato()),
                    candidato.getNombre(),
                    candidato.getApellido(),
                    claveAcceso));
        }

        // Aca no generamos los bloques eso se hace cuando un candidato ingresa con su clave ahi se procesa la generacion y seleccion de preguntas
        cuestionarioRepository.saveAll(cuestionarios);

        return listaRespuesta;
    }

    private String generarClaveAcceso() {
        StringBuilder sb = new StringBuilder(LONGITUD_CLAVE);
        for (int i = 0; i < LONGITUD_CLAVE; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}
