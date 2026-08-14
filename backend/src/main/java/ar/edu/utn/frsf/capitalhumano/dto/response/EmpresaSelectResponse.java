package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmpresaSelectResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name) {

    public String nombre() {
        return name;
    }
}
