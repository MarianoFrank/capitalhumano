package ar.edu.utn.frsf.capitalhumano.model.cuestionario;

import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
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
    @JoinColumn(name = "block_id", nullable = false)
    private Bloque block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Pregunta question;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "obtained_score")
    private Double obtainedScore;

    @OneToMany(mappedBy = "questionItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOpcion> optionItems = new ArrayList<>();

    // Métodos en español
    public Bloque getBloque() {
        return block;
    }

    public void setBloque(Bloque bloque) {
        this.block = bloque;
    }

    public Pregunta getPregunta() {
        return question;
    }

    public void setPregunta(Pregunta pregunta) {
        this.question = pregunta;
    }

    public Integer getOrdenVisualizacion() {
        return displayOrder;
    }

    public void setOrdenVisualizacion(Integer ordenVisualizacion) {
        this.displayOrder = ordenVisualizacion;
    }

    public Double getPuntajeObtenido() {
        return obtainedScore;
    }

    public void setPuntajeObtenido(Double puntajeObtenido) {
        this.obtainedScore = puntajeObtenido;
    }

    public List<ItemOpcion> getItemsOpcion() {
        return optionItems;
    }

    public void setItemsOpcion(List<ItemOpcion> itemsOpcion) {
        this.optionItems = itemsOpcion;
    }
}
