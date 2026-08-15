package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;

@Entity
@Data
@Table(name = "cuestionarios")
public class Cuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Candidato candidato;

    @Column(name = "clave_acceso", nullable = false, unique = true)
    private String claveAcceso;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    // NO es cuando finaliza el cuestionario, sino hasta cuando el candidato puede acceder a él.
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "cantidad_accesos", nullable = false)
    private Integer cantidadAccesos = 0;

    @Column(name = "puntaje_total")
    private Double puntajeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCuestionario estado = EstadoCuestionario.ACTIVE;

    @OneToMany(mappedBy = "cuestionario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroBloque ASC")
    private List<Bloque> bloques = new ArrayList<>();

    @OneToMany(mappedBy = "cuestionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuntajeCompetencia> puntajesCompetencias = new ArrayList<>();
}
