package ar.edu.utn.frsf.capitalhumano.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.frsf.capitalhumano.model.Consultor;

import java.util.Optional;

public interface ConsultorRepository extends JpaRepository<Consultor, Integer> {

    Optional<Consultor> findByUsername(String username);

    boolean existsByUsername(String username);
}
