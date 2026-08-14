package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidatoLoginResponse(
        @JsonProperty("questionnaireId") Long questionnaireId) {

    public Long idCuestionario() {
        return questionnaireId;
    }
}
