package ar.edu.utn.frsf.capitalhumano.dto.request;

import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PreguntaRequest(
        @NotNull(message = "El ID del factor es obligatorio") @JsonProperty("factorId") Long factorId,

        @NotBlank(message = "El nombre de la pregunta no puede estar vacío") @Size(max = 255, message = "El nombre no puede superar los 255 caracteres") @JsonProperty("name") String name,

        @NotBlank(message = "El texto de la pregunta es obligatorio") @JsonProperty("text") String text,

        @JsonProperty("description") String description,

        @NotNull(message = "El tipo de pregunta es obligatorio") @JsonProperty("type") TipoPregunta type,

        @NotNull(message = "Debe enviar al menos una opción") @Size(min = 2, message = "Una pregunta debe tener al menos 2 opciones") @JsonProperty("options") List<@Valid OpcionRequest> options) {

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

    public List<OpcionRequest> opciones() {
        return options;
    }
}
