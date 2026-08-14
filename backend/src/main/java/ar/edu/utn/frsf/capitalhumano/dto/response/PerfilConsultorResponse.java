package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PerfilConsultorResponse(
        @JsonProperty("username") String username,
        @JsonProperty("role") String role,
        @JsonProperty("name") String name,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("legajo") String legajo) {

    public String nombre() {
        return name;
    }

    public String apellido() {
        return lastName;
    }

    public String rol() {
        return role;
    }
}
