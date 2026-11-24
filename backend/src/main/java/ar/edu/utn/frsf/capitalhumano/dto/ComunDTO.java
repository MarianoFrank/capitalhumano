package ar.edu.utn.frsf.capitalhumano.dto;

import ar.edu.utn.frsf.capitalhumano.model.Competencia;
import ar.edu.utn.frsf.capitalhumano.model.Empresa;
import ar.edu.utn.frsf.capitalhumano.model.Factor;

public final class ComunDTO {

    private ComunDTO() {}

    // Respuesta: Elemento genérico para listas desplegables (Empresas, Competencias, Factores)
    public record ItemSeleccion(
            Long id,
            String nombre
    ) {
        public static ItemSeleccion from(Empresa empresa) {
            if (empresa == null) return null;
            return new ItemSeleccion(empresa.getId(), empresa.getNombre());
        }

        public static ItemSeleccion from(Competencia competencia) {
            if (competencia == null) return null;
            return new ItemSeleccion(competencia.getId(), competencia.getNombre());
        }

        public static ItemSeleccion from(Factor factor) {
            if (factor == null) return null;
            return new ItemSeleccion(factor.getId(), factor.getNombre());
        }
    }

    // Respuesta: Perfil de consultor autenticado vía LDAP
    public record PerfilConsultor(
            String nombreUsuario,
            String rol,
            String nombre,
            String apellido,
            String legajo
    ) {}
}
