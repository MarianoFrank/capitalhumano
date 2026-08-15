package ar.edu.utn.frsf.capitalhumano.dto;

import java.time.LocalDateTime;
import java.util.List;

public final class CuestionarioDTO {

    private CuestionarioDTO() {}

    // Respuesta: Metadatos para el arranque de la evaluación
    public record Inicio(
            Long idCuestionario,
            int totalBloques,
            int bloqueActual,
            int duracionMinutos,
            String estado,
            LocalDateTime fechaInicio
    ) {}

    // Respuesta: Bloque activo con su lote de preguntas
    public record Bloque(
            Long id,
            List<ItemPregunta> itemsPregunta
    ) {}

    // Respuesta: Pregunta individual dentro de un bloque
    public record ItemPregunta(
            Long id,
            int ordenVisualizacion,
            String texto,
            boolean esMultiple,
            List<ItemOpcion> itemsOpcion
    ) {}

    // Respuesta: Opción individual dentro de un ítem de pregunta
    public record ItemOpcion(
            Long id,
            int ordenVisualizacion,
            String texto,
            Boolean estaRespondida
    ) {}
}
