package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.FactorMapper;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import ar.edu.utn.frsf.capitalhumano.repository.FactorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactorService {

    private final FactorRepository factorRepository;
    private final FactorMapper factorMapper;

    public FactorService(FactorRepository factorRepository, FactorMapper factorMapper) {
        this.factorRepository = factorRepository;
        this.factorMapper = factorMapper;
    }

    public List<ComunDTO.ItemSeleccion> obtenerFactoresParaSelect(Long idCompetencia) {
        List<Factor> factores;
        if (idCompetencia != null) {
            factores = factorRepository.findByCompetenciaIdAndFechaBajaIsNullOrderByNombreAsc(idCompetencia);
        } else {
            factores = factorRepository.findByFechaBajaIsNullOrderByNombreAsc();
        }
        return factorMapper.aItemsSeleccion(factores);
    }
}
