package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ItemPreguntaResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("displayOrder") int displayOrder,
        @JsonProperty("text") String text,
        @JsonProperty("isMultiple") boolean isMultiple,
        @JsonProperty("optionItems") List<ItemOpcionResponse> optionItems) {

    public int ordenVisualizacion() {
        return displayOrder;
    }

    public String texto() {
        return text;
    }

    public boolean esMultiple() {
        return isMultiple;
    }

    public List<ItemOpcionResponse> itemsOpcion() {
        return optionItems;
    }
}
