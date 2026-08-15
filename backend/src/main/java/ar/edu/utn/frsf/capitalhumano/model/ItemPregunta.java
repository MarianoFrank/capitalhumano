package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "items_pregunta")
public class ItemPregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloque_id", nullable = false)
    private Bloque bloque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @Column(name = "orden_visualizacion", nullable = false)
    private Integer ordenVisualizacion;

    @Column(name = "puntaje_obtenido")
    private Double puntajeObtenido;

    @OneToMany(mappedBy = "itemPregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOpcion> itemsOpcion = new ArrayList<>();
}
