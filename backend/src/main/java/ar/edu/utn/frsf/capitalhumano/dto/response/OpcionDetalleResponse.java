package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpcionDetalleResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("displayOrder") Integer displayOrder,
        @JsonProperty("weight") Integer weight,
        @JsonProperty("text") String text) {

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
