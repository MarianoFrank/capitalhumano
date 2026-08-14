package ar.edu.utn.frsf.capitalhumano.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenerarPreguntaRequest(
        @JsonProperty("competencyName") String competencyName,
        @JsonProperty("factorName") String factorName,
        @JsonProperty("questionName") String questionName,
        @JsonProperty("description") String description,
        @JsonProperty("extraContext") String extraContext) {

    public String nombreCompetencia() {
        return competencyName;
    }

    public String nombreFactor() {
        return factorName;
    }

    public String nombrePregunta() {
        return questionName;
    }

    public String descripcion() {
        return description;
    }

    public String contextoExtra() {
        return extraContext;
    }
}
