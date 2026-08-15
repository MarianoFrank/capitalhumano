package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "puntajes_factor")
public class PuntajeFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puntaje_competencia_id", nullable = false)
    private PuntajeCompetencia puntajeCompetencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factor_id", nullable = false)
    private Factor factor;

    @Column(name = "puntaje", nullable = false)
    private Double puntaje;
}
