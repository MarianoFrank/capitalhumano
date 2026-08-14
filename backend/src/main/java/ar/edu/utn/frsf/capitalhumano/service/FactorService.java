package ar.edu.utn.frsf.capitalhumano.service;

import java.util.List;
import org.springframework.stereotype.Service;

import ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse;
import ar.edu.utn.frsf.capitalhumano.repository.FactorRepository;

@Service
public class FactorService {

    private final FactorRepository factorRepository;

    public FactorService(FactorRepository factorRepository) {
        this.factorRepository = factorRepository;
    }

    public List<SelectItemResponse> obtenerFactoresParaSelect(Long idCompetencia) {
        if (idCompetencia != null) {
            return factorRepository.findForSelectByCompetencyId(idCompetencia);
        }
        return factorRepository.findAllForSelect();
    }
}
