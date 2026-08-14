package ar.edu.utn.frsf.capitalhumano.model.cuestionario;

import ar.edu.utn.frsf.capitalhumano.model.Competencia;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "puntajes_competencia")
public class PuntajeCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id", nullable = false)
    private Cuestionario questionnaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    private Competencia competency;

    @Column(name = "score", nullable = false)
    private Double score;

    // Relación hacia los puntajes individuales de cada factor de esta competencia
    @OneToMany(mappedBy = "competencyScore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuntajeFactor> factorScores = new ArrayList<>();

    // Métodos en español
    public Cuestionario getCuestionario() {
        return questionnaire;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.questionnaire = cuestionario;
    }

    public Competencia getCompetencia() {
        return competency;
    }

    public void setCompetencia(Competencia competencia) {
        this.competency = competencia;
    }

    public Double getPuntaje() {
        return score;
    }

    public void setPuntaje(Double puntaje) {
        this.score = puntaje;
    }

    public List<PuntajeFactor> getPuntajesFactores() {
        return factorScores;
    }

    public void setPuntajesFactores(List<PuntajeFactor> puntajesFactores) {
        this.factorScores = puntajesFactores;
    }
}
