package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PuestoResumenResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("code") String code,
        @JsonProperty("positionName") String positionName,
        @JsonProperty("companyName") String companyName,
        @JsonProperty("totalCandidates") int totalCandidates,
        @JsonProperty("completedEvaluations") int completedEvaluations) {

    public String codigo() {
        return code;
    }

    public String nombrePuesto() {
        return positionName;
    }

    public String nombreEmpresa() {
        return companyName;
    }

    public int totalCandidatos() {
        return totalCandidates;
    }

    public int evaluacionesCompletadas() {
        return completedEvaluations;
    }
}
