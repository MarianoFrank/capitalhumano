package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import ar.edu.utn.frsf.capitalhumano.model.enums.Genero;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoDocumento;

@Entity
@Data
@Table(name = "candidatos")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_candidato", unique = true, nullable = false)
    private Long numeroCandidato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", nullable = false)
    private Genero genero;

    @Column(nullable = false)
    private String email;

    @Column(name = "escolaridad")
    private String escolaridad;

    @Column(name = "nacionalidad")
    private String nacionalidad;
}
