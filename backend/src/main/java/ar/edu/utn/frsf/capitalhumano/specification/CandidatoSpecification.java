package ar.edu.utn.frsf.capitalhumano.specification;

import org.springframework.data.jpa.domain.Specification;

import ar.edu.utn.frsf.capitalhumano.model.Candidato;

public final class CandidatoSpecification {

    private CandidatoSpecification() {
    }

    public static Specification<Candidato> tieneNombre(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("nombre")), "%" + nombre.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Candidato> tieneApellido(String apellido) {
        return (root, query, cb) -> {
            if (apellido == null || apellido.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("apellido")), "%" + apellido.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Candidato> tieneNumeroCandidato(Long numeroCandidato) {
        return (root, query, cb) -> {
            if (numeroCandidato == null) {
                return null;
            }
            return cb.equal(root.get("numeroCandidato"), numeroCandidato);
        };
    }

    public static Specification<Candidato> conFiltros(String nombre, String apellido, Long numeroCandidato) {
        return Specification
                .where(tieneNombre(nombre))
                .and(tieneApellido(apellido))
                .and(tieneNumeroCandidato(numeroCandidato));
    }
}
