package ar.edu.utn.frsf.capitalhumano.model.cuestionario;

import ar.edu.utn.frsf.capitalhumano.model.Opcion;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "items_opcion")
public class ItemOpcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_item_id", nullable = false)
    private ItemPregunta questionItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Opcion option;

    @Column(name = "is_answered", nullable = false)
    private Boolean isAnswered = false;

    // Métodos en español
    public ItemPregunta getItemPregunta() {
        return questionItem;
    }

    public void setItemPregunta(ItemPregunta itemPregunta) {
        this.questionItem = itemPregunta;
    }

    public Opcion getOpcion() {
        return option;
    }

    public void setOpcion(Opcion opcion) {
        this.option = opcion;
    }

    public Boolean getEstaRespondida() {
        return isAnswered;
    }

    public void setEstaRespondida(Boolean estaRespondida) {
        this.isAnswered = estaRespondida;
    }
}
