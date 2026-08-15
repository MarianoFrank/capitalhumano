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
    @MapsId("puestoId")
    @JoinColumn(name = "puesto_id")
    private Puesto puesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("competenciaId")
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    @Column(name = "ponderacion_requerida")
    private Integer ponderacionRequerida;
}
