package ar.edu.utn.frsf.capitalhumano.dto;

public final class ComunDTO {

    private ComunDTO() {}

    // Respuesta: Elemento genérico para listas desplegables (Empresas, Competencias, Factores)
    public record ItemSeleccion(
            Long id,
            String nombre
    ) {}

    // Respuesta: Perfil de consultor autenticado vía LDAP
    public record PerfilConsultor(
            String nombreUsuario,
            String rol,
            String nombre,
            String apellido,
            String legajo
    ) {}
}
