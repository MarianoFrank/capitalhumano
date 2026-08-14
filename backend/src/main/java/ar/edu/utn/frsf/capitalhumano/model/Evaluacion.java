package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultor consultant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Puesto position;

    @Column(nullable = false, unique = true)
    private String code;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "close_date", nullable = false)
    private LocalDateTime closeDate;

    @Column(name = "duration", nullable = false)
    private Integer duration; // Duración en minutos

    // Métodos en español
    public Consultor getConsultor() {
        return consultant;
    }

    public void setConsultor(Consultor consultor) {
        this.consultant = consultor;
    }

    public Puesto getPuesto() {
        return position;
    }

    public void setPuesto(Puesto puesto) {
        this.position = puesto;
    }

    public String getCodigo() {
        return code;
    }

    public void setCodigo(String codigo) {
        this.code = codigo;
    }

    public LocalDateTime getFechaCreacion() {
        return createdAt;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.createdAt = fechaCreacion;
    }

    public LocalDateTime getFechaCierre() {
        return closeDate;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.closeDate = fechaCierre;
    }

    public Integer getDuracion() {
        return duration;
    }

    public void setDuracion(Integer duracion) {
        this.duration = duracion;
    }
}
