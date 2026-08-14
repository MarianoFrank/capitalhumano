package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpcionGeneradaResponse(
        @JsonProperty("text") String text,
        @JsonProperty("weight") Integer weight) {

    public String texto() {
        return text;
    }

    public Integer ponderacion() {
        return weight;
    }
}
