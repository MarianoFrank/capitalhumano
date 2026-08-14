package ar.edu.utn.frsf.capitalhumano.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidatoCsvRequest(
        @JsonProperty("nro_candidato") String candidateNumber,
        @JsonProperty("tipo_documento") String documentType,
        @JsonProperty("nro_documento") String documentNumber,
        @JsonProperty("nombre") String firstName,
        @JsonProperty("apellido") String lastName,
        @JsonProperty("fecha_nacimiento") String birthDate,
        @JsonProperty("genero") String gender,
        @JsonProperty("email") String email,
        @JsonProperty("escolaridad") String educationLevel,
        @JsonProperty("nacionalidad") String nationality) {

    public String numeroCandidato() {
        return candidateNumber;
    }

    public String tipoDocumento() {
        return documentType;
    }

    public String numeroDocumento() {
        return documentNumber;
    }

    public String nombre() {
        return firstName;
    }

    public String apellido() {
        return lastName;
    }

    public String fechaNacimiento() {
        return birthDate;
    }

    public String genero() {
        return gender;
    }

    public String escolaridad() {
        return educationLevel;
    }

    public String nacionalidad() {
        return nationality;
    }
}
