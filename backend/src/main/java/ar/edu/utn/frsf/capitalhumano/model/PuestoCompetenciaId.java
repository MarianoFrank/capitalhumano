package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// Llave compuesta para la relación muchos a muchos entre Puesto y Competencia
@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PuestoCompetenciaId implements Serializable {

    @Column(name = "puesto_id")
    private Long puestoId;

    @Column(name = "competencia_id")
    private Long competenciaId;
}
