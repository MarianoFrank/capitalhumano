package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.PuestoDTO;
import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import ar.edu.utn.frsf.capitalhumano.repository.CompetenciaRepository;
import ar.edu.utn.frsf.capitalhumano.repository.PuestoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PuestoService {

    private final PuestoRepository puestoRepository;
    private final CompetenciaRepository competenciaRepository;

    public PuestoService(PuestoRepository puestoRepository,
                         CompetenciaRepository competenciaRepository) {
        this.puestoRepository = puestoRepository;
        this.competenciaRepository = competenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<PuestoDTO.Seleccion> obtenerPuestosParaSelect() {
        List<Puesto> puestos = puestoRepository.findAllActivePositions();
        Set<Long> competenciasValidasIds = new HashSet<>(competenciaRepository.findValidCompetencyIds());

        return PuestoDTO.Seleccion.fromList(puestos, competenciasValidasIds);
    }
}
