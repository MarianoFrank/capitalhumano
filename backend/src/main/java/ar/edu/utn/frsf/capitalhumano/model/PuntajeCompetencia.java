package ar.edu.utn.frsf.capitalhumano.model;

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
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @Column(name = "puntaje", nullable = false)
    private Double puntaje;

    // Relación hacia los puntajes individuales de cada factor de esta competencia
    @OneToMany(mappedBy = "puntajeCompetencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuntajeFactor> puntajesFactores = new ArrayList<>();
}
