package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PuestoRepository extends JpaRepository<Puesto, Long>, JpaSpecificationExecutor<Puesto> {

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
}
