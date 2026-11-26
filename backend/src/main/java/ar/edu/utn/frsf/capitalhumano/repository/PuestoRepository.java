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
        JOIN FETCH p.empresa
        LEFT JOIN FETCH p.competencias cp
        LEFT JOIN FETCH cp.competencia
        WHERE p.fechaBaja IS NULL
    """)
    List<Puesto> findAllActivePositions();

    @Query(
        value = """
            SELECT DISTINCT p
            FROM Puesto p
            LEFT JOIN FETCH p.empresa e
            WHERE p.fechaBaja IS NULL
              AND EXISTS (SELECT 1 FROM Evaluacion ev WHERE ev.puesto = p)
              AND (:idEmpresa IS NULL OR e.id = :idEmpresa)
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%')))
              AND (:codigo IS NULL OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', CAST(:codigo AS string), '%')))
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p)
            FROM Puesto p
            LEFT JOIN p.empresa e
            WHERE p.fechaBaja IS NULL
              AND EXISTS (SELECT 1 FROM Evaluacion ev WHERE ev.puesto = p)
              AND (:idEmpresa IS NULL OR e.id = :idEmpresa)
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%')))
              AND (:codigo IS NULL OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', CAST(:codigo AS string), '%')))
        """
    )
    Page<Puesto> buscarParaReporte(
            @Param("idEmpresa") Long idEmpresa,
            @Param("nombre") String nombre,
            @Param("codigo") String codigo,
            Pageable pageable
    );
}
