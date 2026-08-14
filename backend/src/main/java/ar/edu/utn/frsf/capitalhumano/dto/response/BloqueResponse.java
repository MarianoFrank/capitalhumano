package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BloqueResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("questionItems") List<ItemPreguntaResponse> questionItems) {

    public List<ItemPreguntaResponse> itemsPregunta() {
        return questionItems;
    }
}
