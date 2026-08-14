package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.response.BloqueResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.ItemOpcionResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.ItemPreguntaResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.IniciarCuestionarioResponse;
import ar.edu.utn.frsf.capitalhumano.event.CuestionarioCompletadoEvent;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.cuestionario.*;
import ar.edu.utn.frsf.capitalhumano.repository.PreguntaRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CuestionarioService {

    private final CuestionarioRepository cuestionarioRepository;
    private final PreguntaRepository preguntaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final int PREGUNTAS_POR_BLOQUE = 3;

    public CuestionarioService(CuestionarioRepository cuestionarioRepository,
            PreguntaRepository preguntaRepository, ApplicationEventPublisher eventPublisher) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.preguntaRepository = preguntaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public IniciarCuestionarioResponse iniciarCuestionario(Long idCuestionario) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        int totalBloques;
        int numeroBloqueActual = 1;

        if (cuestionario.getEstado() == EstadoCuestionario.IN_PROGRESS) {
            cuestionario.setUltimoAcceso(LocalDateTime.now());
            cuestionario.setCantidadAccesos(cuestionario.getCantidadAccesos() + 1);
            cuestionarioRepository.save(cuestionario);

            totalBloques = cuestionario.getBloques().size();
            numeroBloqueActual = calcularBloqueActual(cuestionario);

            return new IniciarCuestionarioResponse(
                    cuestionario.getId(),
                    totalBloques,
                    numeroBloqueActual,
                    cuestionario.getEvaluacion().getDuracion(),
                    cuestionario.getEstado().name(),
                    cuestionario.getFechaInicio());
        }

        if (cuestionario.getEstado() != EstadoCuestionario.ACTIVE) {
            throw new IllegalArgumentException("El cuestionario no se encuentra habilitado para ser completado");
        }

        // ----------------------------------------------------------------------
        // -- Si es la primera vez que entra GENERAMOS los bloques y preguntas --
        // ----------------------------------------------------------------------

        List<Pregunta> selectedQuestions = new ArrayList<>();

        // Iteramos sobre las competencias del puesto y seleccionamos 2 preguntas
        // aleatorias de cada factor asociado a la competencia
        for (PuestoCompetencia pc : cuestionario.getEvaluacion().getPuesto().getCompetencias()) {
            Competencia competency = pc.getCompetencia();
            for (Factor factor : competency.getFactores()) {
                List<Pregunta> factorQuestions = preguntaRepository.findActiveByFactorId(factor.getId());
                if (factorQuestions.size() >= 2) {
                    Collections.shuffle(factorQuestions);
                    selectedQuestions.add(factorQuestions.get(0));
                    selectedQuestions.add(factorQuestions.get(1));
                }
            }
        }

        // Mezclamos las preguntas seleccionadas
        Collections.shuffle(selectedQuestions);

        // Armado de bloques
        List<Bloque> blocks = new ArrayList<>();
        int blockNumber = 1;

        // Creamos el primer bloque
        Bloque currentBlock = new Bloque();
        currentBlock.setCuestionario(cuestionario);
        currentBlock.setNumeroBloque(blockNumber);
        currentBlock.setItemsPregunta(new ArrayList<>());

        for (int i = 0; i < selectedQuestions.size(); i++) {

            // Cada vez que llegamos a PREGUNTAS_POR_BLOQUE, creamos un nuevo bloque
            if (i > 0 && i % PREGUNTAS_POR_BLOQUE == 0) {
                blocks.add(currentBlock);
                blockNumber++;

                currentBlock = new Bloque();
                currentBlock.setCuestionario(cuestionario);
                currentBlock.setNumeroBloque(blockNumber);
                currentBlock.setItemsPregunta(new ArrayList<>());
            }

            // Obtenemos la pregunta y sus opciones
            Pregunta q = selectedQuestions.get(i);
            List<Opcion> opt = q.getOpciones();

            // Creamos los items
            ItemPregunta item = new ItemPregunta();
            item.setBloque(currentBlock);
            item.setPregunta(q);
            item.setOrdenVisualizacion((i % PREGUNTAS_POR_BLOQUE) + 1); // El orden de visualización dentro del bloque
            item.setItemsOpcion(new ArrayList<>());

            for (Opcion op : opt) {
                ItemOpcion optItem = new ItemOpcion();
                optItem.setItemPregunta(item);
                optItem.setOpcion(op);
                optItem.setEstaRespondida(false);
                item.getItemsOpcion().add(optItem);
            }

            currentBlock.getItemsPregunta().add(item);
        }

        // Agregamos el último bloque si la cantidad de preguntas no es múltiplo de PREGUNTAS_POR_BLOQUE
        if (!currentBlock.getItemsPregunta().isEmpty()) {
            blocks.add(currentBlock);
        }

        // Guardamos los bloques en el cuestionario y actualizamos su estado
        cuestionario.getBloques().addAll(blocks);
        cuestionario.setEstado(EstadoCuestionario.IN_PROGRESS);
        cuestionario.setFechaInicio(LocalDateTime.now());
        cuestionario.setUltimoAcceso(LocalDateTime.now());
        cuestionario.setCantidadAccesos(cuestionario.getCantidadAccesos() + 1);

        cuestionarioRepository.save(cuestionario);

        return new IniciarCuestionarioResponse(
                cuestionario.getId(),
                blocks.size(),
                1,
                cuestionario.getEvaluacion().getDuracion(),
                cuestionario.getEstado().name(),
                cuestionario.getFechaInicio());
    }

    @Transactional(readOnly = true)
    public BloqueResponse obtenerBloquePorNumero(Long idCuestionario, int numeroBloque) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        if (cuestionario.getEstado() != EstadoCuestionario.IN_PROGRESS) {
            throw new IllegalArgumentException("El cuestionario no está en curso.");
        }

        int bloquePermitido = calcularBloqueActual(cuestionario);

        if (numeroBloque != bloquePermitido) {
            throw new IllegalArgumentException(
                    "No tienes permiso para acceder a este bloque. Debes completar los bloques en orden.");
        }

        Bloque block = cuestionario.getBloques().stream()
                .filter(b -> b.getNumeroBloque() == numeroBloque)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bloque no encontrado."));

        List<ItemPreguntaResponse> itemDTOs = new ArrayList<>();

        for (ItemPregunta item : block.getItemsPregunta()) {
            List<ItemOpcionResponse> optionDTOs = new ArrayList<>();

            for (ItemOpcion optItem : item.getItemsOpcion()) {
                optionDTOs.add(new ItemOpcionResponse(
                        optItem.getId(),
                        optItem.getOpcion().getOrdenVisualizacion(),
                        optItem.getOpcion().getTexto(),
                        optItem.getEstaRespondida()));
            }

            boolean isMultiple = item.getPregunta().getTipo() != null &&
                    item.getPregunta().getTipo().name().toUpperCase().contains("MULTIPLE");

            itemDTOs.add(new ItemPreguntaResponse(
                    item.getId(),
                    item.getOrdenVisualizacion(),
                    item.getPregunta().getTexto(),
                    isMultiple,
                    optionDTOs));
        }

        return new BloqueResponse(block.getId(), itemDTOs);
    }

    // Calcula el bloque actual que el candidato debería estar completando
    public int calcularBloqueActual(Cuestionario cuestionario) {
        for (Bloque block : cuestionario.getBloques()) {
            boolean bloqueIncompleto = block.getItemsPregunta().stream()
                    .anyMatch(item -> item.getItemsOpcion().stream()
                            .noneMatch(optItem -> Boolean.TRUE.equals(optItem.getEstaRespondida())));

            if (bloqueIncompleto) {
                return block.getNumeroBloque();
            }
        }
        return cuestionario.getBloques().size();
    }

    @Transactional
    public void enviarRespuestasBloque(Long idCuestionario, int numeroBloque, Map<Long, List<Long>> respuestas) {
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

        // Si el bloque enviado es el último, marcamos el cuestionario como COMPLETED
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

        // Buscamos los que están activos o en proceso
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
                // Nunca entró
                q.setEstado(EstadoCuestionario.NOT_ANSWERED);
            } else if (q.getEstado() == EstadoCuestionario.IN_PROGRESS) {
                // Entró pero lo dejó por la mitad
                q.setEstado(EstadoCuestionario.INCOMPLETE);
            }
            q.setFechaFin(ahora);
        }

        cuestionarioRepository.saveAll(cuestionariosVencidos);

        System.out.println("Cuestionarios vencidos actualizados: " + cuestionariosVencidos.size());
    }
}
