package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long>, JpaSpecificationExecutor<Pregunta> {

    long countByFactorId(Long factorId);

    @EntityGraph(attributePaths = { "opciones" })
    Optional<Pregunta> findByIdAndFechaBajaIsNull(Long id);

    @EntityGraph(attributePaths = { "opciones" })
    @Query("""
        SELECT DISTINCT q
        FROM Pregunta q
        WHERE q.fechaBaja IS NULL
          AND q.factor.id = :factorId
    """)
    List<Pregunta> findActiveByFactorId(@Param("factorId") Long factorId);

    List<Pregunta> findByFactorIdAndFechaBajaIsNull(Long factorId);
}
