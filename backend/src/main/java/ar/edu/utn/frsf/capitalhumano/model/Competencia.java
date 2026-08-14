package ar.edu.utn.frsf.capitalhumano.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.capitalhumano.model.enums.TipoCompetencia;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "competencias")
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false)
    private String code;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCompetencia type;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "competency", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Factor> factors = new ArrayList<>();

    // Métodos auxiliares en español
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

    public TipoCompetencia getTipo() {
        return type;
    }

    public void setTipo(TipoCompetencia tipo) {
        this.type = tipo;
    }

    public LocalDateTime getFechaBaja() {
        return deletedAt;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.deletedAt = fechaBaja;
    }

    public List<Factor> getFactores() {
        return factors;
    }

    public void setFactores(List<Factor> factores) {
        this.factors = factores;
    }
}
