package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.request.OpcionRequest;
import ar.edu.utn.frsf.capitalhumano.dto.request.PreguntaRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.OpcionDetalleResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PreguntaDetalleResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PreguntaResumenResponse;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import ar.edu.utn.frsf.capitalhumano.model.Opcion;
import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import ar.edu.utn.frsf.capitalhumano.repository.FactorRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PreguntaRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreguntaService {

    private static final String PREGUNTA_NO_ENCONTRADA_MSG = "La pregunta no existe o fue eliminada";
    private static final String FACTOR_NO_ENCONTRADO_MSG = "El factor especificado no existe";
    private final PreguntaRepository preguntaRepository;
    private final FactorRepository factorRepository;

    public PreguntaService(PreguntaRepository preguntaRepository, FactorRepository factorRepository) {
        this.preguntaRepository = preguntaRepository;
        this.factorRepository = factorRepository;
    }

    public Page<PreguntaResumenResponse> obtenerTodasLasPreguntasResumen(Long idCompetencia, Long idFactor, String nombrePregunta,
            Pageable pageable) {
        return preguntaRepository.findAllSummaryQuestionsWithFilters(idCompetencia, idFactor, nombrePregunta, pageable);
    }

    public PreguntaDetalleResponse obtenerPreguntaPorId(Long idPregunta) {
        Pregunta q = preguntaRepository.findByIdAndDeletedAtIsNull(idPregunta)
                .orElseThrow(() -> new IllegalArgumentException(PREGUNTA_NO_ENCONTRADA_MSG));

        List<OpcionDetalleResponse> optionsDTO = q.getOpciones().stream()
                .map(opt -> new OpcionDetalleResponse(opt.getId(), opt.getOrdenVisualizacion(), opt.getPonderacion(), opt.getTexto()))
                .toList();

        return new PreguntaDetalleResponse(
                q.getId(),
                q.getFactor().getId(),
                q.getNombre(),
                q.getTexto(),
                q.getDescripcion(),
                q.getTipo(),
                optionsDTO);
    }

    @Transactional
    public Pregunta crearPregunta(PreguntaRequest dto) {
        Factor factor = factorRepository.findById(dto.idFactor())
                .orElseThrow(() -> new IllegalArgumentException(FACTOR_NO_ENCONTRADO_MSG));

        Pregunta question = new Pregunta();
        question.setFactor(factor);
        question.setNombre(dto.nombre());
        question.setTexto(dto.texto());
        question.setDescripcion(dto.descripcion());
        question.setTipo(dto.tipo());

        question.setVersion(1);

        for (OpcionRequest optionDto : dto.opciones()) {
            Opcion option = new Opcion();
            option.setOrdenVisualizacion(optionDto.ordenVisualizacion());
            option.setPonderacion(optionDto.ponderacion());
            option.setTexto(optionDto.texto());

            option.setPregunta(question);
            question.getOpciones().add(option);
        }

        return preguntaRepository.save(question);
    }

    @Transactional
    public Pregunta actualizarPregunta(Long idPregunta, PreguntaRequest dto) {
        // Solo trae la pregunta si NO está borrada
        Pregunta existingQuestion = preguntaRepository.findByIdAndDeletedAtIsNull(idPregunta)
                .orElseThrow(() -> new IllegalArgumentException(PREGUNTA_NO_ENCONTRADA_MSG));

        existingQuestion.setNombre(dto.nombre());
        existingQuestion.setTexto(dto.texto());
        existingQuestion.setDescripcion(dto.descripcion());
        existingQuestion.setTipo(dto.tipo());

        if (!existingQuestion.getFactor().getId().equals(dto.idFactor())) {
            Factor newFactor = factorRepository.findById(dto.idFactor())
                    .orElseThrow(() -> new IllegalArgumentException(FACTOR_NO_ENCONTRADO_MSG));
            existingQuestion.setFactor(newFactor);
        }

        existingQuestion.getOpciones().clear();

        for (OpcionRequest optionDto : dto.opciones()) {
            Opcion option = new Opcion();
            option.setOrdenVisualizacion(optionDto.ordenVisualizacion());
            option.setPonderacion(optionDto.ponderacion());
            option.setTexto(optionDto.texto());

            option.setPregunta(existingQuestion);
            existingQuestion.getOpciones().add(option);
        }

        return preguntaRepository.save(existingQuestion);
    }

    @Transactional
    public void eliminarPregunta(Long idPregunta) {
        Pregunta existingQuestion = preguntaRepository.findByIdAndDeletedAtIsNull(idPregunta)
                .orElseThrow(() -> new IllegalArgumentException(PREGUNTA_NO_ENCONTRADA_MSG));

        existingQuestion.setFechaBaja(LocalDateTime.now());

        preguntaRepository.save(existingQuestion);
    }
}
