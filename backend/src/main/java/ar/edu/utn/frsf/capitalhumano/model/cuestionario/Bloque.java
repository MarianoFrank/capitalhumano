package ar.edu.utn.frsf.capitalhumano.model.cuestionario;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "bloques")
public class Bloque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id", nullable = false)
    private Cuestionario questionnaire;

    @Column(name = "block_number", nullable = false)
    private Integer blockNumber;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPregunta> questionItems = new ArrayList<>();

    // Métodos en español
    public Cuestionario getCuestionario() {
        return questionnaire;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.questionnaire = cuestionario;
    }

    public Integer getNumeroBloque() {
        return blockNumber;
    }

    public void setNumeroBloque(Integer numeroBloque) {
        this.blockNumber = numeroBloque;
    }

    public List<ItemPregunta> getItemsPregunta() {
        return questionItems;
    }

    public void setItemsPregunta(List<ItemPregunta> itemsPregunta) {
        this.questionItems = itemsPregunta;
    }
}
