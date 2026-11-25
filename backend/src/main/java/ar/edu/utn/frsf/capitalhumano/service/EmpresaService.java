package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<ComunDTO.ItemSeleccion> obtenerEmpresasParaSelect() {
        return empresaRepository.findByFechaBajaIsNullOrderByNombreAsc()
                .stream()
                .map(ComunDTO.ItemSeleccion::from)
                .toList();
    }
}
