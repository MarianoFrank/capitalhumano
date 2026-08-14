package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidatoResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("candidateNumber") Long candidateNumber,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("documentType") String documentType,
        @JsonProperty("documentNumber") String documentNumber,
        @JsonProperty("email") String email) {

    public Long numeroCandidato() {
        return candidateNumber;
    }

    public String nombre() {
        return firstName;
    }

    public String apellido() {
        return lastName;
    }

    public String tipoDocumento() {
        return documentType;
    }

    public String numeroDocumento() {
        return documentNumber;
    }
}
