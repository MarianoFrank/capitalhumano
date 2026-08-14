package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    // Traemos los puestos junto con sus empresas y competencias para evitar el problema N+1
    @Query("""
        SELECT DISTINCT p
        FROM Puesto p
        JOIN FETCH p.company
        LEFT JOIN FETCH p.competencies cp
        LEFT JOIN FETCH cp.competency
        WHERE p.deletedAt IS NULL
    """)
    List<Puesto> findAllActivePositions();

    // Traemos los puestos solo si tienen evaluaciones asociadas y no están dados de baja
    @Query("""
        SELECT p
        FROM Puesto p
        WHERE p.deletedAt IS NULL
          AND EXISTS (SELECT 1 FROM Evaluacion e WHERE e.position.id = p.id)
          AND (:companyId IS NULL OR p.company.id = :companyId)
          AND (:positionName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:positionName AS string), '%')))
          AND (:code IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', CAST(:code AS string), '%')))
    """)
    Page<Puesto> findWithFilters(
            @Param("companyId") Long companyId,
            @Param("positionName") String positionName,
            @Param("code") String code,
            Pageable pageable);
}
