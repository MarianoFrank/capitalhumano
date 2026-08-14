package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record ReporteOrdenMeritoResponse(
        @JsonProperty("companyName") String companyName,
        @JsonProperty("positionName") String positionName,
        @JsonProperty("printedBy") String printedBy,
        @JsonProperty("printedAt") LocalDateTime printedAt,
        @JsonProperty("approvedCandidates") List<ReporteCandidatoResponse> approvedCandidates,
        @JsonProperty("rejectedOrIncompleteCandidates") List<ReporteCandidatoResponse> rejectedOrIncompleteCandidates) {

    public String nombreEmpresa() {
        return companyName;
    }

    public String nombrePuesto() {
        return positionName;
    }

    public String emitidoPor() {
        return printedBy;
    }

    public LocalDateTime fechaEmision() {
        return printedAt;
    }

    public List<ReporteCandidatoResponse> candidatosAprobados() {
        return approvedCandidates;
    }

    public List<ReporteCandidatoResponse> candidatosRechazadosOIncompletos() {
        return rejectedOrIncompleteCandidates;
    }
}
