package ar.edu.utn.frsf.capitalhumano.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OpcionRequest(
        @NotNull(message = "El orden de visualización es obligatorio.") @Min(value = 1, message = "El orden de visualización debe ser al menos 1.") @JsonProperty("displayOrder") Integer displayOrder,

        @NotNull(message = "El peso (weight) es obligatorio.") @Min(value = 0, message = "El peso de la opción no puede ser negativo.") @JsonProperty("weight") Integer weight,

        @NotBlank(message = "El texto de la opción no puede estar vacío.") @Size(max = 500, message = "El texto de la opción no puede superar los 500 caracteres.") @JsonProperty("text") String text) {

    public Integer ordenVisualizacion() {
        return displayOrder;
    }

    public Integer ponderacion() {
        return weight;
    }

    public String texto() {
        return text;
    }
}
