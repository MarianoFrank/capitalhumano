package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CuestionarioRepository extends JpaRepository<Cuestionario, Long>, JpaSpecificationExecutor<Cuestionario> {

    Optional<Cuestionario> findByClaveAcceso(String claveAcceso);

    @Query("""
        SELECT q
        FROM Cuestionario q
        WHERE q.evaluacion.fechaCierre < :now
          AND q.estado IN (:states)
    """)
    List<Cuestionario> findExpiredQuestionnaires(
            @Param("now") LocalDateTime now,
            @Param("states") List<EstadoCuestionario> states);

    // Trae los cuestionarios de una evaluación particular
    List<Cuestionario> findByEvaluacionId(Long evaluacionId);

    // Trae los cuestionarios de TODAS las evaluaciones de un puesto particular
    List<Cuestionario> findByEvaluacionPuestoId(Long puestoId);
}
