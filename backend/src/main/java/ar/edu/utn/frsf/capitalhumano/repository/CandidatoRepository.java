package ar.edu.utn.frsf.capitalhumano.repository;

import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long>, JpaSpecificationExecutor<Candidato> {

    Optional<Candidato> findByNumeroCandidato(Long numeroCandidato);

    Optional<Candidato> findByNumeroDocumento(String numeroDocumento);

    List<Candidato> findByNumeroCandidatoIn(List<Long> numerosCandidatos);
}
