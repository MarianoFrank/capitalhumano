package ar.edu.utn.frsf.capitalhumano.repository;

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

    @EntityGraph(attributePaths = { "opciones" })
    Optional<Pregunta> findByIdAndFechaBajaIsNull(Long id);

    boolean existsByFactorIdAndNombreIgnoreCaseAndFechaBajaIsNull(Long factorId, String nombre);

    boolean existsByFactorIdAndTextoIgnoreCaseAndFechaBajaIsNull(Long factorId, String texto);

    boolean existsByFactorIdAndNombreIgnoreCaseAndFechaBajaIsNullAndIdNot(Long factorId, String nombre, Long id);

    boolean existsByFactorIdAndTextoIgnoreCaseAndFechaBajaIsNullAndIdNot(Long factorId, String texto, Long id);

    @EntityGraph(attributePaths = { "opciones" })
    @Query("""
        SELECT DISTINCT q
        FROM Pregunta q
        WHERE q.fechaBaja IS NULL
          AND q.factor.id = :factorId
    """)
    List<Pregunta> findActiveByFactorId(@Param("factorId") Long factorId);

    @Query(
        value = """
            SELECT DISTINCT p
            FROM Pregunta p
            JOIN FETCH p.factor f
            JOIN FETCH f.competencia c
            WHERE p.fechaBaja IS NULL
              AND (:idCompetencia IS NULL OR c.id = :idCompetencia)
              AND (:idFactor IS NULL OR f.id = :idFactor)
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p)
            FROM Pregunta p
            JOIN p.factor f
            JOIN f.competencia c
            WHERE p.fechaBaja IS NULL
              AND (:idCompetencia IS NULL OR c.id = :idCompetencia)
              AND (:idFactor IS NULL OR f.id = :idFactor)
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        """
    )
    Page<Pregunta> buscarConFiltros(
            @Param("idCompetencia") Long idCompetencia,
            @Param("idFactor") Long idFactor,
            @Param("nombre") String nombre,
            Pageable pageable
    );
}
