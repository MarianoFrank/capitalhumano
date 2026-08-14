package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public LocalDateTime getFechaBaja() {
        return deletedAt;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.deletedAt = fechaBaja;
    }
}
