package ar.edu.utn.frsf.capitalhumano.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidatoLoginRequest(
        @JsonAlias({"claveAcceso", "code", "codigo"})
        @JsonProperty("accessCode") String accessCode) {

    public String codigoAcceso() {
        return accessCode;
    }
}
