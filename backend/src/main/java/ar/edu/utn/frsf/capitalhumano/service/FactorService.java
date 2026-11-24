package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import ar.edu.utn.frsf.capitalhumano.repository.FactorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactorService {

    private final FactorRepository factorRepository;

    public FactorService(FactorRepository factorRepository) {
        this.factorRepository = factorRepository;
    }

    public List<ComunDTO.ItemSeleccion> obtenerFactoresParaSelect(Long idCompetencia) {
        List<Factor> factores;
        if (idCompetencia != null) {
            factores = factorRepository.findByCompetenciaIdAndFechaBajaIsNullOrderByNombreAsc(idCompetencia);
        } else {
            factores = factorRepository.findByFechaBajaIsNullOrderByNombreAsc();
        }
        return factores.stream()
                .map(ComunDTO.ItemSeleccion::from)
                .toList();
    }
}
