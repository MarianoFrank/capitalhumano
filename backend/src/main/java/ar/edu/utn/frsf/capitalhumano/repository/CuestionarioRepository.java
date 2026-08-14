package ar.edu.utn.frsf.capitalhumano.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.cuestionario.Cuestionario;

public interface CuestionarioRepository extends JpaRepository<Cuestionario, Long> {

    Optional<Cuestionario> findByAccessKey(String accessKey);

    @Query("""
        SELECT q
        FROM Cuestionario q
        WHERE q.evaluation.closeDate < :now
          AND q.state IN (:states)
    """)
    List<Cuestionario> findExpiredQuestionnaires(
            @Param("now") LocalDateTime now,
            @Param("states") List<EstadoCuestionario> states);

    // Trae los cuestionarios de una evaluación particular
    List<Cuestionario> findByEvaluationId(Long evaluationId);

    // Trae los cuestionarios de TODAS las evaluaciones de un puesto particular
    List<Cuestionario> findByEvaluationPositionId(Long positionId);
}
