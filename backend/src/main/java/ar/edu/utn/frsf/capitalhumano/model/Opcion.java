package ar.edu.utn.frsf.capitalhumano.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "opciones")
@Getter
@Setter
@NoArgsConstructor
public class Opcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    @JsonBackReference
    private Pregunta pregunta;

    @Column(name = "orden_visualizacion", nullable = false)
    private Integer ordenVisualizacion;

    @Column(name = "ponderacion", nullable = false)
    private Integer ponderacion;

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String texto;
}
