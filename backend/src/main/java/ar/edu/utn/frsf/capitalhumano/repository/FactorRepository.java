package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FactorRepository extends JpaRepository<Factor, Long> {

    @Query("""
        SELECT new ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse(f.id, f.name)
        FROM Factor f
        WHERE f.deletedAt IS NULL
        ORDER BY f.name
    """)
    List<SelectItemResponse> findAllForSelect();

    @Query("""
        SELECT new ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse(f.id, f.name)
        FROM Factor f
        WHERE f.deletedAt IS NULL
          AND f.competency.id = :competencyId
        ORDER BY f.name
    """)
    List<SelectItemResponse> findForSelectByCompetencyId(@Param("competencyId") Long competencyId);
}
