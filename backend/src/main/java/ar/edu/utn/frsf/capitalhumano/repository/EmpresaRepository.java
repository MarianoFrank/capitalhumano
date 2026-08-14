package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.dto.response.EmpresaSelectResponse;
import ar.edu.utn.frsf.capitalhumano.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("""
        SELECT new ar.edu.utn.frsf.capitalhumano.dto.response.EmpresaSelectResponse(e.id, e.name)
        FROM Empresa e
        ORDER BY e.name
    """)
    List<EmpresaSelectResponse> findAllForSelect();
}
