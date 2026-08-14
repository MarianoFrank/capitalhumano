package ar.edu.utn.frsf.capitalhumano.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PuestoSelectResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("company") String company,
        @JsonProperty("competencies") List<CompetenciaCantidadResponse> competencies) {

    public String nombre() {
        return name;
    }

    public String empresa() {
        return company;
    }

    public List<CompetenciaCantidadResponse> competencias() {
        return competencies;
    }
}
