package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record PreguntaResumenResponse(
        @JsonProperty("id") Long id,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm") @JsonProperty("updatedAt") LocalDateTime updatedAt,
        @JsonProperty("competencyName") String competencyName,
        @JsonProperty("factorName") String factorName,
        @JsonProperty("questionName") String questionName) {

    public LocalDateTime fechaModificacion() {
        return updatedAt;
    }

    public String nombreCompetencia() {
        return competencyName;
    }

    public String nombreFactor() {
        return factorName;
    }

    public String nombrePregunta() {
        return questionName;
    }
}
