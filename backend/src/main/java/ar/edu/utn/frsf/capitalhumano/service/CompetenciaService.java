package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.CompetenciaMapper;
import ar.edu.utn.frsf.capitalhumano.model.Competencia;
import ar.edu.utn.frsf.capitalhumano.repository.CompetenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;
    private final CompetenciaMapper competenciaMapper;

    public CompetenciaService(CompetenciaRepository competenciaRepository, CompetenciaMapper competenciaMapper) {
        this.competenciaRepository = competenciaRepository;
        this.competenciaMapper = competenciaMapper;
    }

    public List<ComunDTO.ItemSeleccion> obtenerCompetenciasParaSelect() {
        List<Competencia> competencias = competenciaRepository.findByFechaBajaIsNullOrderByNombreAsc();
        return competenciaMapper.aItemsSeleccion(competencias);
    }
}
