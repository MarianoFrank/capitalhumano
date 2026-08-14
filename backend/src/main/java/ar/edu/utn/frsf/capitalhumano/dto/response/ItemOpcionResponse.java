package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemOpcionResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("displayOrder") int displayOrder,
        @JsonProperty("text") String text,
        @JsonProperty("isAnswered") Boolean isAnswered) {

    public int ordenVisualizacion() {
        return displayOrder;
    }

    public String texto() {
        return text;
    }

    public Boolean estaRespondida() {
        return isAnswered;
    }
}
