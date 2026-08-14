package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

// Llave compuesta para la relación muchos a muchos entre Puesto y Competencia
@Embeddable
@Data
@EqualsAndHashCode
public class PuestoCompetenciaId implements Serializable {

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "competency_id")
    private Long competencyId;

    public Long getPuestoId() {
        return positionId;
    }

    public void setPuestoId(Long puestoId) {
        this.positionId = puestoId;
    }

    public Long getCompetenciaId() {
        return competencyId;
    }

    public void setCompetenciaId(Long competenciaId) {
        this.competencyId = competenciaId;
    }
}
