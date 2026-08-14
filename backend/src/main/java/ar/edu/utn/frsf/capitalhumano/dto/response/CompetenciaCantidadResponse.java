package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompetenciaCantidadResponse(
        @JsonProperty("name") String name,
        @JsonProperty("weightingRequired") Integer weightingRequired,
        @JsonProperty("meetsCondition") boolean meetsCondition) {

    public String nombre() {
        return name;
    }

    public Integer ponderacionRequerida() {
        return weightingRequired;
    }

    public boolean cumpleCondicion() {
        return meetsCondition;
    }
}
