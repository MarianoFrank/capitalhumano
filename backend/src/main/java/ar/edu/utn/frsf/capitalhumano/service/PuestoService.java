package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.response.CompetenciaCantidadResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PuestoSelectResponse;
import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import ar.edu.utn.frsf.capitalhumano.repository.PuestoRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CompetenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class PuestoService {

    private final PuestoRepository puestoRepository;
    private final CompetenciaRepository competenciaRepository;

    public PuestoService(PuestoRepository puestoRepository, CompetenciaRepository competenciaRepository) {
        this.puestoRepository = puestoRepository;
        this.competenciaRepository = competenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<PuestoSelectResponse> obtenerPuestosParaSelect() {
        // Buscamos todos los puestos activos
        List<Puesto> puestosActivos = puestoRepository.findAllActivePositions();

        // Buscamos los IDs de las competencias que CUMPLEN la condición
        Set<Long> compIdsValidos = new HashSet<>(competenciaRepository.findValidCompetencyIds());

        return puestosActivos.stream().map(puesto -> {

            List<CompetenciaCantidadResponse> competencias = puesto.getCompetencias().stream()
                    .map(cp -> {
                        Long compId = cp.getCompetencia().getId();
                        // Verificamos si la competencia está en el Set de competencias válidas
                        boolean esValida = compIdsValidos.contains(compId);

                        return new CompetenciaCantidadResponse(
                                cp.getCompetencia().getNombre(),
                                cp.getPonderacionRequerida(),
                                esValida);
                    })
                    .collect(Collectors.toList());

            return new PuestoSelectResponse(
                    puesto.getId(),
                    puesto.getNombre(),
                    puesto.getEmpresa().getNombre(),
                    competencias);

        }).collect(Collectors.toList());
    }
}
