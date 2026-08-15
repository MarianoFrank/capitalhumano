package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.EvaluacionMapper;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CandidatoRepository;
import ar.edu.utn.frsf.capitalhumano.repository.ConsultorRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import ar.edu.utn.frsf.capitalhumano.repository.EvaluacionRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PreguntaRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PuestoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final PuestoRepository puestoRepository;
    private final CandidatoRepository candidatoRepository;
    private final CuestionarioRepository cuestionarioRepository;
    private final ConsultorRepository consultorRepository;
    private final PreguntaRepository preguntaRepository;
    private final EvaluacionMapper evaluacionMapper;

    private static final String CARACTERES_CLAVE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CLAVE = 8;
    private static final int PREGUNTAS_POR_BLOQUE = 5;
    private final SecureRandom random = new SecureRandom();

    public EvaluacionService(EvaluacionRepository evaluacionRepository,
            PuestoRepository puestoRepository,
            CandidatoRepository candidatoRepository,
            CuestionarioRepository cuestionarioRepository,
            ConsultorRepository consultorRepository,
            PreguntaRepository preguntaRepository,
            EvaluacionMapper evaluacionMapper) {
        this.evaluacionRepository = evaluacionRepository;
        this.puestoRepository = puestoRepository;
        this.candidatoRepository = candidatoRepository;
        this.cuestionarioRepository = cuestionarioRepository;
        this.consultorRepository = consultorRepository;
        this.preguntaRepository = preguntaRepository;
        this.evaluacionMapper = evaluacionMapper;
    }

    @Transactional
    public List<EvaluacionDTO.ClaveGenerada> generarEvaluacion(EvaluacionDTO.Generar peticion) {
        String usernameConsultor = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Consultor> consultor = consultorRepository.findByUsername(usernameConsultor);

        if (!consultor.isPresent()) {
            throw new RuntimeException("Consultor no encontrado");
        }

        Puesto puesto = puestoRepository.findById(peticion.idPuesto())
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setCodigo("EVAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        evaluacion.setPuesto(puesto);
        evaluacion.setConsultor(consultor.get());
        evaluacion.setDuracion(60);
        evaluacion.setFechaCreacion(LocalDateTime.now());
        evaluacion.setFechaCierre(LocalDateTime.now().plusDays(7));

        evaluacion = evaluacionRepository.save(evaluacion);

        List<EvaluacionDTO.ClaveGenerada> clavesGeneradas = new ArrayList<>();

        for (Long candidatoId : peticion.idsCandidatos()) {
            Candidato candidato = candidatoRepository.findById(candidatoId)
                    .orElseThrow(() -> new RuntimeException("Candidato con ID " + candidatoId + " no encontrado"));

            String claveAcceso = generarClaveAccesoUnica();

            Cuestionario cuestionario = new Cuestionario();
            cuestionario.setEvaluacion(evaluacion);
            cuestionario.setCandidato(candidato);
            cuestionario.setClaveAcceso(claveAcceso);
            cuestionario.setEstado(EstadoCuestionario.ACTIVE);
            cuestionario.setCantidadAccesos(0);

            // Generamos la estructura completa de bloques, preguntas y opciones
            generarEstructuraCuestionario(cuestionario, puesto);

            cuestionarioRepository.save(cuestionario);

            clavesGeneradas.add(evaluacionMapper.aClaveGenerada(candidato, claveAcceso));
        }

        return clavesGeneradas;
    }

    public void generarEstructuraCuestionario(Cuestionario cuestionario, Puesto puesto) {
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

                        if (!preguntasFactor.isEmpty()) {
                            Collections.shuffle(preguntasFactor, random);
                            int cantidadTomar = Math.min(2, preguntasFactor.size());
                            preguntasSeleccionadas.addAll(preguntasFactor.subList(0, cantidadTomar));
                        }
                    }
                }
            }
        }

        // Si por alguna razón el puesto no tiene preguntas específicas, fallback a preguntas activas
        if (preguntasSeleccionadas.isEmpty()) {
            List<Pregunta> todasActivas = new ArrayList<>(preguntaRepository.findAll().stream()
                    .filter(p -> p.getFechaBaja() == null)
                    .toList());
            if (!todasActivas.isEmpty()) {
                Collections.shuffle(todasActivas, random);
                preguntasSeleccionadas.addAll(todasActivas.subList(0, Math.min(10, todasActivas.size())));
            }
        }

        List<Bloque> bloques = new ArrayList<>();
        int numeroBloque = 1;

        for (int i = 0; i < preguntasSeleccionadas.size(); i += PREGUNTAS_POR_BLOQUE) {
            int fin = Math.min(i + PREGUNTAS_POR_BLOQUE, preguntasSeleccionadas.size());
            List<Pregunta> preguntasBloque = preguntasSeleccionadas.subList(i, fin);

            Bloque bloque = new Bloque();
            bloque.setCuestionario(cuestionario);
            bloque.setNumeroBloque(numeroBloque++);
            bloque.setItemsPregunta(new ArrayList<>());

            int ordenPregunta = 1;
            for (Pregunta preg : preguntasBloque) {
                ItemPregunta itemPregunta = new ItemPregunta();
                itemPregunta.setBloque(bloque);
                itemPregunta.setPregunta(preg);
                itemPregunta.setOrdenVisualizacion(ordenPregunta++);
                itemPregunta.setItemsOpcion(new ArrayList<>());

                if (preg.getOpciones() != null) {
                    for (Opcion opc : preg.getOpciones()) {
                        ItemOpcion itemOpcion = new ItemOpcion();
                        itemOpcion.setItemPregunta(itemPregunta);
                        itemOpcion.setOpcion(opc);
                        itemOpcion.setEstaRespondida(false);
                        itemPregunta.getItemsOpcion().add(itemOpcion);
                    }
                }

                bloque.getItemsPregunta().add(itemPregunta);
            }

            bloques.add(bloque);
        }

        if (cuestionario.getBloques() != null) {
            cuestionario.getBloques().clear();
            cuestionario.getBloques().addAll(bloques);
        } else {
            cuestionario.setBloques(new ArrayList<>(bloques));
        }
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
