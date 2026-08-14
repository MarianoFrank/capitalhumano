package ar.edu.utn.frsf.capitalhumano.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse;
import ar.edu.utn.frsf.capitalhumano.repository.CompetenciaRepository;

@Service
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;

    public CompetenciaService(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    public List<SelectItemResponse> obtenerCompetenciasParaSelect() {
        return competenciaRepository.findAllForSelect();
    }
}
