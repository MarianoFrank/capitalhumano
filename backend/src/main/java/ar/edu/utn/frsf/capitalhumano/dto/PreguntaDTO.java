package ar.edu.utn.frsf.capitalhumano.dto;

import ar.edu.utn.frsf.capitalhumano.model.Opcion;
import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class PreguntaDTO {

    private PreguntaDTO() {}

    // Petición: Creación o modificación de una pregunta
    public record Guardar(
            @NotNull(message = "El ID del factor es obligatorio")
            Long idFactor,

            @NotBlank(message = "El nombre de la pregunta no puede estar vacío")
            @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
            String nombre,

            @NotBlank(message = "El texto de la pregunta es obligatorio")
            String texto,

            String descripcion,

            @NotNull(message = "El tipo de pregunta es obligatorio")
            TipoPregunta tipo,

            @NotNull(message = "Debe enviar al menos una opción")
            @Size(min = 2, message = "Una pregunta debe tener al menos 2 opciones")
            List<@Valid OpcionGuardar> opciones
    ) {}

    // Petición: Opción dentro del guardado de una pregunta
    public record OpcionGuardar(
            @NotNull(message = "El orden de visualización es obligatorio.")
            @Min(value = 1, message = "El orden de visualización debe ser al menos 1.")
            Integer ordenVisualizacion,

            @NotNull(message = "La ponderación es obligatoria.")
            @Min(value = 0, message = "La ponderación de la opción no puede ser negativa.")
            Integer ponderacion,

            @NotBlank(message = "El texto de la opción no puede estar vacío.")
            @Size(max = 500, message = "El texto de la opción no puede superar los 500 caracteres.")
            String texto
    ) {}

    // Petición: Solicitud de generación asistida a la IA
    public record IaPeticion(
            String nombreCompetencia,
            String nombreFactor,
            String nombrePregunta,
            String descripcion,
            String contextoExtra
    ) {}

    // Respuesta: Resumen para la grilla de administración de preguntas
    public record Resumen(
            Long id,
            @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fechaModificacion,
            String nombreCompetencia,
            String nombreFactor,
            String nombrePregunta
    ) {
        public static Resumen from(Pregunta p) {
            if (p == null) return null;
            String competencia = (p.getFactor() != null && p.getFactor().getCompetencia() != null)
                    ? p.getFactor().getCompetencia().getNombre() : null;
            String factor = (p.getFactor() != null) ? p.getFactor().getNombre() : null;
            return new Resumen(p.getId(), p.getFechaModificacion(), competencia, factor, p.getNombre());
        }
    }

    // Respuesta: Vista detallada de una pregunta existente
    public record Detalle(
            Long id,
            Long idFactor,
            String nombre,
            String texto,
            String descripcion,
            TipoPregunta tipo,
            List<OpcionDetalle> opciones
    ) {
        public static Detalle from(Pregunta p) {
            if (p == null) return null;
            List<OpcionDetalle> ops = (p.getOpciones() != null)
                    ? p.getOpciones().stream().map(OpcionDetalle::from).toList()
                    : Collections.emptyList();
            Long factorId = (p.getFactor() != null) ? p.getFactor().getId() : null;
            return new Detalle(p.getId(), factorId, p.getNombre(), p.getTexto(), p.getDescripcion(), p.getTipo(), ops);
        }
    }

    // Respuesta: Detalle de una opción de respuesta
    public record OpcionDetalle(
            Long id,
            Integer ordenVisualizacion,
            Integer ponderacion,
            String texto
    ) {
        public static OpcionDetalle from(Opcion o) {
            if (o == null) return null;
            return new OpcionDetalle(o.getId(), o.getOrdenVisualizacion(), o.getPonderacion(), o.getTexto());
        }
    }

    // Respuesta: Pregunta sugerida por la IA
    public record IaRespuesta(
            String nombrePregunta,
            String descripcion,
            TipoPregunta tipo,
            String texto,
            List<OpcionGenerada> opciones
    ) {}

    // Respuesta: Opción estructurada por la IA
    public record OpcionGenerada(
            String texto,
            Integer ponderacion
    ) {}
}
