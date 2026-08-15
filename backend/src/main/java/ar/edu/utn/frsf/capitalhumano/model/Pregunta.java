package ar.edu.utn.frsf.capitalhumano.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "preguntas")
@Getter
@Setter
@NoArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factor_id", nullable = false)
    private Factor factor;

    // Inmutabilidad por Versionado
    @Column(nullable = false)
    private Integer version;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoPregunta tipo;

    // Se actualiza automáticamente la fecha de actualización cada vez que se modifica la entidad
    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    // Relación One-To-Many con las opciones
    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Opcion> opciones = new ArrayList<>();
}
