package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import ar.edu.utn.frsf.capitalhumano.model.Opcion;
import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import ar.edu.utn.frsf.capitalhumano.repository.FactorRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PreguntaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final FactorRepository factorRepository;

    public PreguntaService(PreguntaRepository preguntaRepository,
                           FactorRepository factorRepository) {
        this.preguntaRepository = preguntaRepository;
        this.factorRepository = factorRepository;
    }

    public Page<PreguntaDTO.Resumen> obtenerTodasLasPreguntasResumen(
            Long idCompetencia, Long idFactor, String nombrePregunta, Pageable pageable) {

        String nombreFiltro = (nombrePregunta != null && !nombrePregunta.isBlank()) ? nombrePregunta.trim() : null;
        Page<Pregunta> preguntasPage = preguntaRepository.buscarConFiltros(idCompetencia, idFactor, nombreFiltro, pageable);
        return preguntasPage.map(PreguntaDTO.Resumen::from);
    }

    public PreguntaDTO.Detalle obtenerPreguntaPorId(Long id) {
        Pregunta pregunta = preguntaRepository.findByIdAndFechaBajaIsNull(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + id));
        return PreguntaDTO.Detalle.from(pregunta);
    }

    @Transactional
    public PreguntaDTO.Detalle crearPregunta(PreguntaDTO.Guardar peticion) {
        Factor factor = factorRepository.findById(peticion.idFactor())
                .filter(f -> f.getFechaBaja() == null)
                .orElseThrow(() -> new IllegalArgumentException("Factor no encontrado o dado de baja con ID: " + peticion.idFactor()));

        validarPreguntaNoDuplicada(factor.getId(), peticion.nombre(), peticion.texto(), null);
        validarPonderacionesOpciones(peticion.opciones());

        Pregunta pregunta = new Pregunta();
        pregunta.setFactor(factor);
        pregunta.setNombre(peticion.nombre().trim());
        pregunta.setTexto(peticion.texto().trim());
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

        Pregunta guardada = preguntaRepository.save(pregunta);
        return PreguntaDTO.Detalle.from(guardada);
    }

    //TODO: al actualizar pregunta si ya esta siendo usada en alguna evaluacion se debe crear la version siguiente
    @Transactional
    public PreguntaDTO.Detalle actualizarPregunta(Long id, PreguntaDTO.Guardar peticion) {
        Pregunta preguntaExistente = preguntaRepository.findByIdAndFechaBajaIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada con ID: " + id));

        Factor factor = factorRepository.findById(peticion.idFactor())
                .filter(f -> f.getFechaBaja() == null)
                .orElseThrow(() -> new IllegalArgumentException("Factor no encontrado o dado de baja con ID: " + peticion.idFactor()));

        validarPreguntaNoDuplicada(factor.getId(), peticion.nombre(), peticion.texto(), id);
        validarPonderacionesOpciones(peticion.opciones());

        preguntaExistente.setFactor(factor);
        preguntaExistente.setNombre(peticion.nombre().trim());
        preguntaExistente.setTexto(peticion.texto().trim());
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

        Pregunta actualizada = preguntaRepository.save(preguntaExistente);
        return PreguntaDTO.Detalle.from(actualizada);
    }

    private void validarPreguntaNoDuplicada(Long idFactor, String nombre, String texto, Long idExcluir) {
        String nombreLimpio = nombre != null ? nombre.trim() : "";
        String textoLimpio = texto != null ? texto.trim() : "";

        boolean existeNombre = (idExcluir == null)
                ? preguntaRepository.existsByFactorIdAndNombreIgnoreCaseAndFechaBajaIsNull(idFactor, nombreLimpio)
                : preguntaRepository.existsByFactorIdAndNombreIgnoreCaseAndFechaBajaIsNullAndIdNot(idFactor, nombreLimpio, idExcluir);
        if (existeNombre) {
            throw new IllegalStateException("Ya existe una pregunta con ese nombre definida para el factor seleccionado.");
        }

        boolean existeTexto = (idExcluir == null)
                ? preguntaRepository.existsByFactorIdAndTextoIgnoreCaseAndFechaBajaIsNull(idFactor, textoLimpio)
                : preguntaRepository.existsByFactorIdAndTextoIgnoreCaseAndFechaBajaIsNullAndIdNot(idFactor, textoLimpio, idExcluir);
        if (existeTexto) {
            throw new IllegalStateException("Ya existe una pregunta con ese texto definida para el factor seleccionado.");
        }
    }

    private void validarPonderacionesOpciones(java.util.List<PreguntaDTO.OpcionGuardar> opciones) {
        int sumaPonderaciones = (opciones == null) ? 0
                : opciones.stream()
                        .mapToInt(op -> op.ponderacion() != null ? op.ponderacion() : 0)
                        .sum();

        if (sumaPonderaciones != 10) {
            throw new IllegalArgumentException("Las ponderaciones de las opciones deben sumar exactamente 10 puntos.");
        }
    }

    @Transactional
    public void eliminarPregunta(Long id) {
        Pregunta pregunta = preguntaRepository.findByIdAndFechaBajaIsNull(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + id));

        pregunta.setFechaBaja(LocalDateTime.now());
        preguntaRepository.save(pregunta);
    }
}
