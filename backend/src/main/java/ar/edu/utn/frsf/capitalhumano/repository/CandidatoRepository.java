package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    Optional<Candidato> findByNumeroCandidato(Long numeroCandidato);

    Optional<Candidato> findByNumeroDocumento(String numeroDocumento);

    List<Candidato> findByNumeroCandidatoIn(List<Long> numerosCandidatos);

    @Query("""
        SELECT c FROM Candidato c
        WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%')))
          AND (:apellido IS NULL OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', CAST(:apellido AS string), '%')))
          AND (:numeroCandidato IS NULL OR c.numeroCandidato = :numeroCandidato)
    """)
    Page<Candidato> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            @Param("numeroCandidato") Long numeroCandidato,
            Pageable pageable
    );
}
