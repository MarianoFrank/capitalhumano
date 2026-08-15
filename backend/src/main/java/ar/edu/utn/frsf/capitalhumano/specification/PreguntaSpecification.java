package ar.edu.utn.frsf.capitalhumano.specification;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import ar.edu.utn.frsf.capitalhumano.model.Pregunta;

public final class PreguntaSpecification {

    private PreguntaSpecification() {
    }

    public static Specification<Pregunta> noEliminada() {
        return (root, query, cb) -> cb.isNull(root.get("fechaBaja"));
    }

    public static Specification<Pregunta> tieneIdCompetencia(Long idCompetencia) {
        return (root, query, cb) -> {
            if (idCompetencia == null) {
                return null;
            }
            return cb.equal(root.join("factor", JoinType.INNER).join("competencia", JoinType.INNER).get("id"),
                    idCompetencia);
        };
    }

    public static Specification<Pregunta> tieneIdFactor(Long idFactor) {
        return (root, query, cb) -> {
            if (idFactor == null) {
                return null;
            }
            return cb.equal(root.join("factor", JoinType.INNER).get("id"), idFactor);
        };
    }

    public static Specification<Pregunta> tieneNombrePregunta(String nombrePregunta) {
        return (root, query, cb) -> {
            if (nombrePregunta == null || nombrePregunta.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("nombre")), "%" + nombrePregunta.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Pregunta> fetchFactorYCompetencia() {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("factor", JoinType.LEFT).fetch("competencia", JoinType.LEFT);
                query.distinct(true);
            }
            return null;
        };
    }

    public static Specification<Pregunta> conFiltros(Long idCompetencia, Long idFactor, String nombrePregunta) {
        return Specification
                .where(noEliminada())
                .and(fetchFactorYCompetencia())
                .and(tieneIdCompetencia(idCompetencia))
                .and(tieneIdFactor(idFactor))
                .and(tieneNombrePregunta(nombrePregunta));
    }
}
