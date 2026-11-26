package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CandidatoRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CompetenciaRepository;
import ar.edu.utn.frsf.capitalhumano.repository.ConsultorRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import ar.edu.utn.frsf.capitalhumano.repository.EvaluacionRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PreguntaRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PuestoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final PuestoRepository puestoRepository;
    private final CandidatoRepository candidatoRepository;
    private final CuestionarioRepository cuestionarioRepository;
    private final ConsultorRepository consultorRepository;
    private final PreguntaRepository preguntaRepository;
    private final CompetenciaRepository competenciaRepository;

    private static final String CARACTERES_CLAVE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CLAVE = 8;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.evaluacion.duracion-minutos:60}")
    private int duracionMinutos = 60;

    @Value("${app.evaluacion.preguntas-por-bloque:5}")
    private int preguntasPorBloque = 5;

    public EvaluacionService(EvaluacionRepository evaluacionRepository,
            PuestoRepository puestoRepository,
            CandidatoRepository candidatoRepository,
            CuestionarioRepository cuestionarioRepository,
            ConsultorRepository consultorRepository,
            PreguntaRepository preguntaRepository,
            CompetenciaRepository competenciaRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.puestoRepository = puestoRepository;
        this.candidatoRepository = candidatoRepository;
        this.cuestionarioRepository = cuestionarioRepository;
        this.consultorRepository = consultorRepository;
        this.preguntaRepository = preguntaRepository;
        this.competenciaRepository = competenciaRepository;
    }

    @Transactional(readOnly = true)
    public void validarCandidatosDisponibles(List<Long> idsCandidatos) {
        if (idsCandidatos == null || idsCandidatos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un candidato.");
        }

        List<Cuestionario> abiertos = cuestionarioRepository.findByCandidatoIdInAndEstadoIn(
                idsCandidatos, List.of(EstadoCuestionario.ACTIVE, EstadoCuestionario.IN_PROGRESS));

        if (!abiertos.isEmpty()) {
            String nombres = abiertos.stream()
                    .map(q -> q.getCandidato().getApellido() + " " + q.getCandidato().getNombre())
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "Los siguientes candidatos poseen cuestionarios activos o en proceso y no pueden ser evaluados nuevamente: " + nombres);
        }
    }

    private void validarCompetenciasEvaluables(Puesto puesto) {
        if (puesto.getCompetencias() == null || puesto.getCompetencias().isEmpty()) {
            throw new IllegalStateException("El puesto seleccionado no posee competencias asociadas para evaluar.");
        }

        Set<Long> validas = new HashSet<>(competenciaRepository.findValidCompetencyIds());
        List<String> invalidas = new ArrayList<>();

        for (PuestoCompetencia pc : puesto.getCompetencias()) {
            Competencia comp = pc.getCompetencia();
            if (comp != null && comp.getFechaBaja() == null && !validas.contains(comp.getId())) {
                invalidas.add(comp.getNombre());
            }
        }

        if (!invalidas.isEmpty()) {
            throw new IllegalStateException(
                    "Las siguientes competencias no pueden ser evaluadas por falta de preguntas activas: "
                            + String.join(", ", invalidas));
        }
    }

    @Transactional
    public List<EvaluacionDTO.ClaveGenerada> generarEvaluacion(EvaluacionDTO.Generar peticion) {
        validarCandidatosDisponibles(peticion.idsCandidatos());

        String usernameConsultor = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Consultor> consultor = consultorRepository.findByUsername(usernameConsultor);

        if (!consultor.isPresent()) {
            throw new IllegalArgumentException("Consultor no encontrado");
        }

        Puesto puesto = puestoRepository.findById(peticion.idPuesto())
                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));

        validarCompetenciasEvaluables(puesto);

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setCodigo("EVAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        evaluacion.setPuesto(puesto);
        evaluacion.setConsultor(consultor.get());
        evaluacion.setDuracion(duracionMinutos);
        evaluacion.setCantPreguntasPorBloque(preguntasPorBloque);
        evaluacion.setFechaCreacion(LocalDateTime.now());
        evaluacion.setFechaCierre(LocalDateTime.now().plusDays(7));

        evaluacion = evaluacionRepository.save(evaluacion);

        // Sortea una vez las preguntas de los factores del puesto para garantizar el mismo modelo en todos los cuestionarios
        List<Pregunta> preguntasModelo = seleccionarPreguntasParaPuesto(puesto);
        List<EvaluacionDTO.ClaveGenerada> clavesGeneradas = new ArrayList<>();

        for (Long candidatoId : peticion.idsCandidatos()) {
            Candidato candidato = candidatoRepository.findById(candidatoId)
                    .orElseThrow(() -> new IllegalArgumentException("Candidato con ID " + candidatoId + " no encontrado"));

            String claveAcceso = generarClaveAccesoUnica();

            Cuestionario cuestionario = new Cuestionario();
            cuestionario.setEvaluacion(evaluacion);
            cuestionario.setCandidato(candidato);
            cuestionario.setClaveAcceso(claveAcceso);
            cuestionario.setEstado(EstadoCuestionario.ACTIVE);
            cuestionario.setCantidadAccesos(0);

            armarBloquesCuestionario(cuestionario, preguntasModelo, evaluacion.getCantPreguntasPorBloque());

            cuestionarioRepository.save(cuestionario);

            clavesGeneradas.add(EvaluacionDTO.ClaveGenerada.from(candidato, claveAcceso));
        }

        return clavesGeneradas;
    }

    public void generarEstructuraCuestionario(Cuestionario cuestionario, Puesto puesto) {
        List<Pregunta> preguntasModelo = seleccionarPreguntasParaPuesto(puesto);
        int porBloque = (cuestionario.getEvaluacion() != null && cuestionario.getEvaluacion().getCantPreguntasPorBloque() != null)
                ? cuestionario.getEvaluacion().getCantPreguntasPorBloque()
                : preguntasPorBloque;
        armarBloquesCuestionario(cuestionario, preguntasModelo, porBloque);
    }

    private List<Pregunta> seleccionarPreguntasParaPuesto(Puesto puesto) {
        List<Pregunta> preguntasSeleccionadas = new ArrayList<>();

        if (puesto.getCompetencias() != null) {
            for (PuestoCompetencia pc : puesto.getCompetencias()) {
                Competencia comp = pc.getCompetencia();
                if (comp == null || comp.getFechaBaja() != null) continue;

                if (comp.getFactores() != null) {
                    for (Factor factor : comp.getFactores()) {
                        if (factor == null || factor.getFechaBaja() != null) continue;

                        List<Pregunta> preguntasFactor = new ArrayList<>(
                                preguntaRepository.findActiveByFactorId(factor.getId()));

                        if (preguntasFactor.size() >= 2) {
                            Collections.shuffle(preguntasFactor, random);
                            preguntasSeleccionadas.addAll(preguntasFactor.subList(0, 2));
                        }
                    }
                }
            }
        }

        Collections.shuffle(preguntasSeleccionadas, random);
        return preguntasSeleccionadas;
    }

    private void armarBloquesCuestionario(Cuestionario cuestionario, List<Pregunta> preguntasSeleccionadas, int porBloque) {
        int tamBloque = Math.max(1, porBloque);
        List<Bloque> bloques = new ArrayList<>();
        int numeroBloque = 1;

        for (int i = 0; i < preguntasSeleccionadas.size(); i += tamBloque) {
            int fin = Math.min(i + tamBloque, preguntasSeleccionadas.size());
            List<Pregunta> preguntasBloque = preguntasSeleccionadas.subList(i, fin);

            Bloque bloque = new Bloque();
            bloque.setCuestionario(cuestionario);
            bloque.setNumeroBloque(numeroBloque++);

            List<ItemPregunta> itemsPregunta = new ArrayList<>();
            int ordenPregunta = 1;

            for (Pregunta pregunta : preguntasBloque) {
                ItemPregunta itemPregunta = new ItemPregunta();
                itemPregunta.setBloque(bloque);
                itemPregunta.setPregunta(pregunta);
                itemPregunta.setOrdenVisualizacion(ordenPregunta++);

                List<ItemOpcion> itemsOpcion = new ArrayList<>();
                if (pregunta.getOpciones() != null) {
                    for (Opcion opcion : pregunta.getOpciones()) {
                        ItemOpcion itemOpcion = new ItemOpcion();
                        itemOpcion.setItemPregunta(itemPregunta);
                        itemOpcion.setOpcion(opcion);
                        itemOpcion.setEstaRespondida(false);
                        itemsOpcion.add(itemOpcion);
                    }
                }
                itemPregunta.setItemsOpcion(itemsOpcion);
                itemsPregunta.add(itemPregunta);
            }

            bloque.setItemsPregunta(itemsPregunta);
            bloques.add(bloque);
        }

        cuestionario.setBloques(bloques);
    }

    private String generarClaveAccesoUnica() {
        String clave;
        do {
            StringBuilder sb = new StringBuilder(LONGITUD_CLAVE);
            for (int i = 0; i < LONGITUD_CLAVE; i++) {
                sb.append(CARACTERES_CLAVE.charAt(random.nextInt(CARACTERES_CLAVE.length())));
            }
            clave = sb.toString();
        } while (cuestionarioRepository.findByClaveAcceso(clave).isPresent());

        return clave;
    }
}
