package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "puesto_competencias")
public class PuestoCompetencia {

    @EmbeddedId
    private PuestoCompetenciaId id = new PuestoCompetenciaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("positionId")
    @JoinColumn(name = "position_id")
    private Puesto position;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("competencyId")
    @JoinColumn(name = "competency_id")
    private Competencia competency;

    @Column(name = "ponderacion_requerida")
    private Integer weightingRequired;

    public Puesto getPuesto() {
        return position;
    }

    public void setPuesto(Puesto puesto) {
        this.position = puesto;
    }

    public Competencia getCompetencia() {
        return competency;
    }

    public void setCompetencia(Competencia competencia) {
        this.competency = competencia;
    }

    public Integer getPonderacionRequerida() {
        return weightingRequired;
    }

    public void setPonderacionRequerida(Integer ponderacionRequerida) {
        this.weightingRequired = ponderacionRequerida;
    }
}
