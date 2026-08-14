package ar.edu.utn.frsf.capitalhumano.model.cuestionario;

import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import ar.edu.utn.frsf.capitalhumano.model.Evaluacion;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cuestionarios")
public class Cuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluacion evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidato candidate;

    @Column(name = "access_key", nullable = false, unique = true)
    private String accessKey;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    // NO es cuando finaliza el cuestionario, sino hasta cuando el candidato puede acceder a él.
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @Column(name = "access_count", nullable = false)
    private Integer accessCount = 0;

    @Column(name = "total_score")
    private Double totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EstadoCuestionario state = EstadoCuestionario.ACTIVE;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("blockNumber ASC")
    private List<Bloque> blocks = new ArrayList<>();

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuntajeCompetencia> competencyScores = new ArrayList<>();

    // Métodos en español
    public Evaluacion getEvaluacion() {
        return evaluation;
    }

    public void setEvaluacion(Evaluacion evaluacion) {
        this.evaluation = evaluacion;
    }

    public Candidato getCandidato() {
        return candidate;
    }

    public void setCandidato(Candidato candidato) {
        this.candidate = candidato;
    }

    public String getClaveAcceso() {
        return accessKey;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.accessKey = claveAcceso;
    }

    public LocalDateTime getFechaInicio() {
        return startedAt;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.startedAt = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return endedAt;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.endedAt = fechaFin;
    }

    public LocalDateTime getUltimoAcceso() {
        return lastAccess;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.lastAccess = ultimoAcceso;
    }

    public Integer getCantidadAccesos() {
        return accessCount;
    }

    public void setCantidadAccesos(Integer cantidadAccesos) {
        this.accessCount = cantidadAccesos;
    }

    public Double getPuntajeTotal() {
        return totalScore;
    }

    public void setPuntajeTotal(Double puntajeTotal) {
        this.totalScore = puntajeTotal;
    }

    public EstadoCuestionario getEstado() {
        return state;
    }

    public void setEstado(EstadoCuestionario estado) {
        this.state = estado;
    }

    public List<Bloque> getBloques() {
        return blocks;
    }

    public void setBloques(List<Bloque> bloques) {
        this.blocks = bloques;
    }

    public List<PuntajeCompetencia> getPuntajesCompetencias() {
        return competencyScores;
    }

    public void setPuntajesCompetencias(List<PuntajeCompetencia> puntajesCompetencias) {
        this.competencyScores = puntajesCompetencias;
    }
}
