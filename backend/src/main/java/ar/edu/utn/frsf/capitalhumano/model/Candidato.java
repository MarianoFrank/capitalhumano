package ar.edu.utn.frsf.capitalhumano.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import ar.edu.utn.frsf.capitalhumano.model.enums.TipoDocumento;
import ar.edu.utn.frsf.capitalhumano.model.enums.Genero;

@Entity
@Data
@Table(name = "candidatos")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nro_candidato", unique = true, nullable = false)
    private Long candidateNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento documentType;

    @Column(name = "nro_documento", nullable = false)
    private String documentNumber;

    @Column(name = "nombre", nullable = false)
    private String firstName;

    @Column(name = "apellido", nullable = false)
    private String lastName;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", nullable = false)
    private Genero gender;

    @Column(nullable = false)
    private String email;

    @Column(name = "escolaridad")
    private String educationLevel;

    @Column(name = "nacionalidad")
    private String nationality;

    // Métodos en español para acceso ordenado
    public Long getNumeroCandidato() {
        return candidateNumber;
    }

    public void setNumeroCandidato(Long numeroCandidato) {
        this.candidateNumber = numeroCandidato;
    }

    public TipoDocumento getTipoDocumento() {
        return documentType;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.documentType = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return documentNumber;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.documentNumber = numeroDocumento;
    }

    public String getNombre() {
        return firstName;
    }

    public void setNombre(String nombre) {
        this.firstName = nombre;
    }

    public String getApellido() {
        return lastName;
    }

    public void setApellido(String apellido) {
        this.lastName = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return birthDate;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.birthDate = fechaNacimiento;
    }

    public Genero getGenero() {
        return gender;
    }

    public void setGenero(Genero genero) {
        this.gender = genero;
    }

    public String getEscolaridad() {
        return educationLevel;
    }

    public void setEscolaridad(String escolaridad) {
        this.educationLevel = escolaridad;
    }

    public String getNacionalidad() {
        return nationality;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nationality = nacionalidad;
    }
}
