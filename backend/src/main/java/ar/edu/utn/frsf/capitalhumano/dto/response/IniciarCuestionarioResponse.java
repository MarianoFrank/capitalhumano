package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record IniciarCuestionarioResponse(
        @JsonProperty("questionnaireId") Long questionnaireId,
        @JsonProperty("totalBlocks") int totalBlocks,
        @JsonProperty("currentBlock") int currentBlock,
        @JsonProperty("durationMinutes") int durationMinutes,
        @JsonProperty("state") String state,
        @JsonProperty("startedAt") LocalDateTime startedAt) {

    public Long idCuestionario() {
        return questionnaireId;
    }

    public int totalBloques() {
        return totalBlocks;
    }

    public int bloqueActual() {
        return currentBlock;
    }

    public int duracionMinutos() {
        return durationMinutes;
    }

    public String estado() {
        return state;
    }

    public LocalDateTime fechaInicio() {
        return startedAt;
    }
}
