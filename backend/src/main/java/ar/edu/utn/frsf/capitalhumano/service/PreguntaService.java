package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.PreguntaMapper;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import ar.edu.utn.frsf.capitalhumano.model.Opcion;
import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import ar.edu.utn.frsf.capitalhumano.repository.FactorRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PreguntaRepository;
import ar.edu.utn.frsf.capitalhumano.specification.PreguntaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final FactorRepository factorRepository;
    private final PreguntaMapper preguntaMapper;

    public PreguntaService(PreguntaRepository preguntaRepository,
                           FactorRepository factorRepository,
                           PreguntaMapper preguntaMapper) {
        this.preguntaRepository = preguntaRepository;
        this.factorRepository = factorRepository;
        this.preguntaMapper = preguntaMapper;
    }

    public Page<PreguntaDTO.Resumen> obtenerTodasLasPreguntasResumen(
            Long idCompetencia, Long idFactor, String nombrePregunta, Pageable pageable) {

        Specification<Pregunta> spec = PreguntaSpecification.conFiltros(idCompetencia, idFactor, nombrePregunta);
        Page<Pregunta> preguntasPage = preguntaRepository.findAll(spec, pageable);
        return preguntaMapper.aPaginaResumen(preguntasPage);
    }

    public PreguntaDTO.Detalle obtenerPreguntaPorId(Long id) {
        Pregunta pregunta = preguntaRepository.findByIdAndFechaBajaIsNull(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + id));
        return preguntaMapper.aDetalle(pregunta);
    }

    @Transactional
    public Pregunta crearPregunta(PreguntaDTO.Guardar peticion) {
        Factor factor = factorRepository.findById(peticion.idFactor())
                .orElseThrow(() -> new RuntimeException("Factor no encontrado con ID: " + peticion.idFactor()));

        Pregunta pregunta = new Pregunta();
        pregunta.setFactor(factor);
        pregunta.setNombre(peticion.nombre());
        pregunta.setTexto(peticion.texto());
        pregunta.setDescripcion(peticion.descripcion());
        pregunta.setTipo(peticion.tipo());
        pregunta.setVersion(1);
        pregunta.setFechaModificacion(LocalDateTime.now());

        if (peticion.opciones() != null) {
            for (PreguntaDTO.OpcionGuardar opDto : peticion.opciones()) {
                Opcion op = new Opcion();
                op.setOrdenVisualizacion(opDto.ordenVisualizacion());
                op.setPonderacion(opDto.ponderacion());
                op.setTexto(opDto.texto());
                op.setPregunta(pregunta);
                pregunta.getOpciones().add(op);
            }
        }

        return preguntaRepository.save(pregunta);
    }

    @Transactional
    public Pregunta actualizarPregunta(Long id, PreguntaDTO.Guardar peticion) {
        Pregunta preguntaExistente = preguntaRepository.findByIdAndFechaBajaIsNull(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + id));

        Factor factor = factorRepository.findById(peticion.idFactor())
                .orElseThrow(() -> new RuntimeException("Factor no encontrado con ID: " + peticion.idFactor()));

        preguntaExistente.setFactor(factor);
        preguntaExistente.setNombre(peticion.nombre());
        preguntaExistente.setTexto(peticion.texto());
        preguntaExistente.setDescripcion(peticion.descripcion());
        preguntaExistente.setTipo(peticion.tipo());
        preguntaExistente.setFechaModificacion(LocalDateTime.now());

        preguntaExistente.getOpciones().clear();
        if (peticion.opciones() != null) {
            for (PreguntaDTO.OpcionGuardar opDto : peticion.opciones()) {
                Opcion op = new Opcion();
                op.setOrdenVisualizacion(opDto.ordenVisualizacion());
                op.setPonderacion(opDto.ponderacion());
                op.setTexto(opDto.texto());
                op.setPregunta(preguntaExistente);
                preguntaExistente.getOpciones().add(op);
            }
        }

        return preguntaRepository.save(preguntaExistente);
    }

    @Transactional
    public void eliminarPregunta(Long id) {
        Pregunta pregunta = preguntaRepository.findByIdAndFechaBajaIsNull(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + id));

        pregunta.setFechaBaja(LocalDateTime.now());
        preguntaRepository.save(pregunta);
    }
}
