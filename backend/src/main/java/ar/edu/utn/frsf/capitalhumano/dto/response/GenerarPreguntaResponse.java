package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GenerarPreguntaResponse(
        @JsonProperty("questionName") String questionName,
        @JsonProperty("description") String description,
        @JsonProperty("type") String type,
        @JsonProperty("text") String text,
        @JsonProperty("options") List<OpcionGeneradaResponse> options) {

    public String nombrePregunta() {
        return questionName;
    }

    public String descripcion() {
        return description;
    }

    public String tipo() {
        return type;
    }

    public String texto() {
        return text;
    }

    public List<OpcionGeneradaResponse> opciones() {
        return options;
    }
}
