package ar.edu.utn.frsf.capitalhumano.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GenerarEvaluacionRequest(
        @JsonProperty("positionId") Long positionId,
        @JsonProperty("candidateIds") List<Long> candidateIds) {

    public Long idPuesto() {
        return positionId;
    }

    public List<Long> idsCandidatos() {
        return candidateIds;
    }
}
