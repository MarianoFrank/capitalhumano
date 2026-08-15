package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Factor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FactorRepository extends JpaRepository<Factor, Long>, JpaSpecificationExecutor<Factor> {

    List<Factor> findByFechaBajaIsNullOrderByNombreAsc();

    List<Factor> findByCompetenciaIdAndFechaBajaIsNullOrderByNombreAsc(Long competenciaId);
}
