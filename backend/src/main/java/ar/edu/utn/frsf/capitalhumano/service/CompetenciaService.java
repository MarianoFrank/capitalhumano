package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.repository.CompetenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;

    public CompetenciaService(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    public List<ComunDTO.ItemSeleccion> obtenerCompetenciasParaSelect() {
        return competenciaRepository.findByFechaBajaIsNullOrderByNombreAsc()
                .stream()
                .map(ComunDTO.ItemSeleccion::from)
                .toList();
    }
}
