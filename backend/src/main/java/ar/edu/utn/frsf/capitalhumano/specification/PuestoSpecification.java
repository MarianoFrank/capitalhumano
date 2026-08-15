package ar.edu.utn.frsf.capitalhumano.specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import ar.edu.utn.frsf.capitalhumano.model.Evaluacion;
import ar.edu.utn.frsf.capitalhumano.model.Puesto;

public final class PuestoSpecification {

    private PuestoSpecification() {
    }

    public static Specification<Puesto> noEliminado() {
        return (root, query, cb) -> cb.isNull(root.get("fechaBaja"));
    }

    public static Specification<Puesto> tieneEvaluaciones() {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Evaluacion> evalRoot = subquery.from(Evaluacion.class);
            subquery.select(cb.literal(1L))
                    .where(cb.equal(evalRoot.get("puesto"), root));
            return cb.exists(subquery);
        };
    }

    public static Specification<Puesto> tieneIdEmpresa(Long idEmpresa) {
        return (root, query, cb) -> {
            if (idEmpresa == null) {
                return null;
            }
            return cb.equal(root.join("empresa", JoinType.INNER).get("id"), idEmpresa);
        };
    }

    public static Specification<Puesto> tieneNombrePuesto(String nombrePuesto) {
        return (root, query, cb) -> {
            if (nombrePuesto == null || nombrePuesto.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("nombre")), "%" + nombrePuesto.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Puesto> tieneCodigo(String codigo) {
        return (root, query, cb) -> {
            if (codigo == null || codigo.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("codigo")), "%" + codigo.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Puesto> fetchEmpresa() {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("empresa", JoinType.LEFT);
                query.distinct(true);
            }
            return null;
        };
    }

    public static Specification<Puesto> conFiltrosReporte(Long idEmpresa, String nombrePuesto, String codigo) {
        return Specification
                .where(noEliminado())
                .and(tieneEvaluaciones())
                .and(fetchEmpresa())
                .and(tieneIdEmpresa(idEmpresa))
                .and(tieneNombrePuesto(nombrePuesto))
                .and(tieneCodigo(codigo));
    }
}
