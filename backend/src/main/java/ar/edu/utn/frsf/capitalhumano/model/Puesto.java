package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "puestos")
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo")
    private String code;

    @Column(name = "nombre")
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Empresa company;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuestoCompetencia> competencies = new ArrayList<>();

    // Métodos en español
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

    public LocalDateTime getFechaBaja() {
        return deletedAt;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.deletedAt = fechaBaja;
    }

    public Empresa getEmpresa() {
        return company;
    }

    public void setEmpresa(Empresa empresa) {
        this.company = empresa;
    }

    public List<PuestoCompetencia> getCompetencias() {
        return competencies;
    }

    public void setCompetencias(List<PuestoCompetencia> competencias) {
        this.competencies = competencias;
    }
}
