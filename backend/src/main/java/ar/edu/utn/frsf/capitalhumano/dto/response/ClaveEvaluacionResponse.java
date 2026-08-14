package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClaveEvaluacionResponse(
        @JsonProperty("candidateNumber") String candidateNumber,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("accessKey") String accessKey) {

    public String numeroCandidato() {
        return candidateNumber;
    }

    public String nombre() {
        return firstName;
    }

    public String apellido() {
        return lastName;
    }

    public String claveAcceso() {
        return accessKey;
    }
}
