package ar.edu.utn.frsf.capitalhumano.dto.response;

import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PreguntaDetalleResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("factorId") Long factorId,
        @JsonProperty("name") String name,
        @JsonProperty("text") String text,
        @JsonProperty("description") String description,
        @JsonProperty("type") TipoPregunta type,
        @JsonProperty("options") List<OpcionDetalleResponse> options) {

    public Long idFactor() {
        return factorId;
    }

    public String nombre() {
        return name;
    }

    public String texto() {
        return text;
    }

    public String descripcion() {
        return description;
    }

    public TipoPregunta tipo() {
        return type;
    }

    public List<OpcionDetalleResponse> opciones() {
        return options;
    }
}
