package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "factores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Factor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Competencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    @JsonIgnore
    private Competencia competency;

    @Column(name = "codigo", unique = true, nullable = false)
    private String code;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Métodos en español
    public Competencia getCompetencia() {
        return competency;
    }

    public void setCompetencia(Competencia competencia) {
        this.competency = competencia;
    }

    public String getCodigo() {
        return code;
    }

    public void setCodigo(String codigo) {
        this.code = codigo;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public String getDescripcion() {
        return description;
    }

    public void setDescripcion(String descripcion) {
        this.description = descripcion;
    }

    public Integer getNumeroOrden() {
        return orderNumber;
    }

    public void setNumeroOrden(Integer numeroOrden) {
        this.orderNumber = numeroOrden;
    }

    public LocalDateTime getFechaBaja() {
        return deletedAt;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.deletedAt = fechaBaja;
    }
}
