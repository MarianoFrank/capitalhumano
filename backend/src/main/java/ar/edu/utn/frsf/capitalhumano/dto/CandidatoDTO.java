package ar.edu.utn.frsf.capitalhumano.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class CandidatoDTO {

    private CandidatoDTO() {}

    // Petición: Carga masiva de candidatos desde CSV
    public record ImportarCsv(
            @JsonProperty("nro_candidato") String numeroCandidato,
            @JsonProperty("tipo_documento") String tipoDocumento,
            @JsonProperty("nro_documento") String numeroDocumento,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("apellido") String apellido,
            @JsonProperty("fecha_nacimiento") String fechaNacimiento,
            @JsonProperty("genero") String genero,
            @JsonProperty("email") String email,
            @JsonProperty("escolaridad") String escolaridad,
            @JsonProperty("nacionalidad") String nacionalidad
    ) {}

    // Petición: Inicio de sesión de candidato con código
    public record IniciarSesion(
            String claveAcceso
    ) {}

    // Respuesta: Confirmación de inicio de sesión de candidato
    public record SesionIniciada(
            Long idCuestionario
    ) {}

    // Respuesta: Resumen para listados y selecciones
    public record Resumen(
            Long id,
            String nombre,
            String apellido,
            Long numeroCandidato
    ) {}

    // Respuesta: Perfil de usuario autenticado
    public record Perfil(
            String nombreUsuario,
            String rol,
            String nombre,
            String apellido
    ) {}
}
