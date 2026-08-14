package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record ReporteCandidatoResponse(
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("docType") String docType,
        @JsonProperty("docNumber") String docNumber,
        @JsonProperty("candidateNumber") String candidateNumber,
        @JsonProperty("state") String state,
        @JsonProperty("score") Double score,
        @JsonProperty("startedAt") LocalDateTime startedAt,
        @JsonProperty("endedAt") LocalDateTime endedAt,
        @JsonProperty("accessCount") Integer accessCount) {

    public String nombre() {
        return firstName;
    }

    public String apellido() {
        return lastName;
    }

    public String tipoDocumento() {
        return docType;
    }

    public String numeroDocumento() {
        return docNumber;
    }

    public String numeroCandidato() {
        return candidateNumber;
    }

    public String estado() {
        return state;
    }

    public Double puntaje() {
        return score;
    }

    public LocalDateTime fechaInicio() {
        return startedAt;
    }

    public LocalDateTime fechaFin() {
        return endedAt;
    }

    public Integer cantidadAccesos() {
        return accessCount;
    }
}
