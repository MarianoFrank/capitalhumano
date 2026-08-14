package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SelectItemResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("nombre") String nombre) {

    public String name() {
        return nombre;
    }
}
