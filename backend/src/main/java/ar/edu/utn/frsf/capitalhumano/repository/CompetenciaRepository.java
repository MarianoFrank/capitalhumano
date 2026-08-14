package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse;
import ar.edu.utn.frsf.capitalhumano.model.Competencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    @Query("""
        SELECT new ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse(c.id, c.name)
        FROM Competencia c
        WHERE c.deletedAt IS NULL
        ORDER BY c.name
    """)
    List<SelectItemResponse> findAllForSelect();

    // Trae SOLO los IDs de las competencias que tienen al menos un factor con 2 o más preguntas activas
    @Query("""
        SELECT f.competency.id
        FROM Pregunta q
        JOIN q.factor f
        WHERE q.deletedAt IS NULL
          AND f.deletedAt IS NULL
        GROUP BY f.competency.id, f.id
        HAVING COUNT(q.id) >= 2
    """)
    List<Long> findValidCompetencyIds();
}
