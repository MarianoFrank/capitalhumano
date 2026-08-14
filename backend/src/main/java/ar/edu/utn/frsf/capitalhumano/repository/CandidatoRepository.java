package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.dto.response.CandidatoResumenResponse;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    Optional<Candidato> findByCandidateNumber(Long candidateNumber);

    Optional<Candidato> findByDocumentNumber(String documentNumber);

    List<Candidato> findByCandidateNumberIn(List<Long> candidateNumbers);

    @Query("""
        SELECT new ar.edu.utn.frsf.capitalhumano.dto.response.CandidatoResumenResponse(
            c.id,
            c.firstName,
            c.lastName,
            c.candidateNumber
        )
        FROM Candidato c
        WHERE (:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', CAST(:firstName AS string), '%')))
          AND (:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', CAST(:lastName AS string), '%')))
          AND (:candidateNumber IS NULL OR c.candidateNumber = :candidateNumber)
    """)
    Page<CandidatoResumenResponse> findSummaryByFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("candidateNumber") Long candidateNumber,
            Pageable pageable);
}
