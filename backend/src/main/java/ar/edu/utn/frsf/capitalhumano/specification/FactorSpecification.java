package ar.edu.utn.frsf.capitalhumano.specification;

import org.springframework.data.jpa.domain.Specification;

import ar.edu.utn.frsf.capitalhumano.model.Factor;

public final class FactorSpecification {

    private FactorSpecification() {
    }

    public static Specification<Factor> noEliminado() {
        return (root, query, cb) -> cb.isNull(root.get("fechaBaja"));
    }

    public static Specification<Factor> tieneIdCompetencia(Long idCompetencia) {
        return (root, query, cb) -> {
            if (idCompetencia == null) {
                return null;
            }
            return cb.equal(root.get("competencia").get("id"), idCompetencia);
        };
    }

    public static Specification<Factor> conFiltros(Long idCompetencia) {
        return Specification
                .where(noEliminado())
                .and(tieneIdCompetencia(idCompetencia));
    }
}
