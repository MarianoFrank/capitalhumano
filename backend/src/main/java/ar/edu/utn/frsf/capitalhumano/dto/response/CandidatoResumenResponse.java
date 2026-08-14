package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidatoResumenResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("candidateNumber") Long candidateNumber) {

    public String nombre() {
        return firstName;
    }

    public String apellido() {
        return lastName;
    }

    public Long numeroCandidato() {
        return candidateNumber;
    }
}
