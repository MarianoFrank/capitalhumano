package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PerfilCandidatoResponse(
        @JsonProperty("username") String username,
        @JsonProperty("role") String role,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName) {

    public String nombre() {
        return firstName;
    }

    public String apellido() {
        return lastName;
    }

    public String rol() {
        return role;
    }
}
