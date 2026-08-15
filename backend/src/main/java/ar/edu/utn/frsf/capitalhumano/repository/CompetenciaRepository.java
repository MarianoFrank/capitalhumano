package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long>, JpaSpecificationExecutor<Competencia> {

    List<Competencia> findByFechaBajaIsNullOrderByNombreAsc();

    // Trae SOLO los IDs de las competencias que tienen al menos un factor con 2 o más preguntas activas
    @Query("""
        SELECT f.competencia.id
        FROM Pregunta q
        JOIN q.factor f
        WHERE q.fechaBaja IS NULL
          AND f.fechaBaja IS NULL
        GROUP BY f.competencia.id, f.id
        HAVING COUNT(q.id) >= 2
    """)
    List<Long> findValidCompetencyIds();
}
