package ar.edu.utn.frsf.capitalhumano.model.cuestionario;

import ar.edu.utn.frsf.capitalhumano.model.Factor;
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
    @JoinColumn(name = "competency_score_id", nullable = false)
    private PuntajeCompetencia competencyScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factor_id", nullable = false)
    private Factor factor;

    @Column(name = "score", nullable = false)
    private Double score;

    public PuntajeCompetencia getPuntajeCompetencia() {
        return competencyScore;
    }

    public void setPuntajeCompetencia(PuntajeCompetencia puntajeCompetencia) {
        this.competencyScore = puntajeCompetencia;
    }

    public Factor getFactor() {
        return factor;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public Double getPuntaje() {
        return score;
    }

    public void setPuntaje(Double puntaje) {
        this.score = puntaje;
    }
}
