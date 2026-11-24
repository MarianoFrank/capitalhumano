package ar.edu.utn.frsf.capitalhumano.dto;

import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;

import java.time.LocalDateTime;
import java.util.Collections;
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
    ) {
        public static Inicio from(Cuestionario c, int totalBloques, int bloqueActual) {
            if (c == null) return null;
            int duracion = (c.getEvaluacion() != null && c.getEvaluacion().getDuracion() != null)
                    ? c.getEvaluacion().getDuracion() : 0;
            String estado = (c.getEstado() != null) ? c.getEstado().name() : null;
            return new Inicio(c.getId(), totalBloques, bloqueActual, duracion, estado, c.getFechaInicio());
        }
    }

    // Respuesta: Bloque activo con su lote de preguntas
    public record Bloque(
            Long id,
            List<ItemPregunta> itemsPregunta
    ) {
        public static Bloque from(ar.edu.utn.frsf.capitalhumano.model.Bloque b) {
            if (b == null) return null;
            List<ItemPregunta> items = (b.getItemsPregunta() != null)
                    ? b.getItemsPregunta().stream().map(ItemPregunta::from).toList()
                    : Collections.emptyList();
            return new Bloque(b.getId(), items);
        }
    }

    // Respuesta: Pregunta individual dentro de un bloque
    public record ItemPregunta(
            Long id,
            int ordenVisualizacion,
            String texto,
            boolean esMultiple,
            List<ItemOpcion> itemsOpcion
    ) {
        public static ItemPregunta from(ar.edu.utn.frsf.capitalhumano.model.ItemPregunta item) {
            if (item == null) return null;
            boolean esMultiple = item.getPregunta() != null
                    && item.getPregunta().getTipo() != null
                    && item.getPregunta().getTipo().name().toUpperCase().contains("MULTIPLE");
            String texto = (item.getPregunta() != null) ? item.getPregunta().getTexto() : null;
            int orden = (item.getOrdenVisualizacion() != null) ? item.getOrdenVisualizacion() : 0;
            List<ItemOpcion> ops = (item.getItemsOpcion() != null)
                    ? item.getItemsOpcion().stream().map(ItemOpcion::from).toList()
                    : Collections.emptyList();

            return new ItemPregunta(item.getId(), orden, texto, esMultiple, ops);
        }
    }

    // Respuesta: Opción individual dentro de un ítem de pregunta
    public record ItemOpcion(
            Long id,
            int ordenVisualizacion,
            String texto,
            Boolean estaRespondida
    ) {
        public static ItemOpcion from(ar.edu.utn.frsf.capitalhumano.model.ItemOpcion io) {
            if (io == null) return null;
            int orden = (io.getOpcion() != null && io.getOpcion().getOrdenVisualizacion() != null)
                    ? io.getOpcion().getOrdenVisualizacion() : 0;
            String texto = (io.getOpcion() != null) ? io.getOpcion().getTexto() : null;
            return new ItemOpcion(io.getId(), orden, texto, io.getEstaRespondida());
        }
    }
}
