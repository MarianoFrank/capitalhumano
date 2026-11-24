package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Factor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactorRepository extends JpaRepository<Factor, Long> {

    List<Factor> findByFechaBajaIsNullOrderByNombreAsc();

    List<Factor> findByCompetenciaIdAndFechaBajaIsNullOrderByNombreAsc(Long competenciaId);
}
