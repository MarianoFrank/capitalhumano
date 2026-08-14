package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EvaluacionResumenResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("description") String description) {

    public String descripcion() {
        return description;
    }
}
