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
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Pregunta question;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String text;

    // Métodos en español
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

    public Integer getPonderacion() {
        return weight;
    }

    public void setPonderacion(Integer ponderacion) {
        this.weight = ponderacion;
    }

    public String getTexto() {
        return text;
    }

    public void setTexto(String texto) {
        this.text = texto;
    }
}
