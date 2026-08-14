package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.dto.response.PreguntaResumenResponse;
import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    long countByFactorId(Long factorId);

    @Query("""
        SELECT new ar.edu.utn.frsf.capitalhumano.dto.response.PreguntaResumenResponse(
            q.id,
            q.updatedAt,
            c.name,
            f.name,
            q.name
        )
        FROM Pregunta q
        JOIN q.factor f
        JOIN f.competency c
        WHERE q.deletedAt IS NULL
          AND (:competencyId IS NULL OR c.id = :competencyId)
          AND (:factorId IS NULL OR f.id = :factorId)
          AND (:questionName IS NULL OR LOWER(q.name) LIKE LOWER(CONCAT('%', CAST(:questionName AS string), '%')))
    """)
    Page<PreguntaResumenResponse> findAllSummaryQuestionsWithFilters(
            @Param("competencyId") Long competencyId,
            @Param("factorId") Long factorId,
            @Param("questionName") String questionName,
            Pageable pageable);

    @EntityGraph(attributePaths = { "options" })
    Optional<Pregunta> findByIdAndDeletedAtIsNull(Long id);

    @Query("""
        SELECT q
        FROM Pregunta q
        WHERE q.deletedAt IS NULL
          AND q.factor.id = :factorId
    """)
    List<Pregunta> findActiveByFactorId(@Param("factorId") Long factorId);
}
