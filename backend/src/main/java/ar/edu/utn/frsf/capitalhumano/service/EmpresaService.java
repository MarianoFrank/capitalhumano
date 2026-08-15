package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.EmpresaMapper;
import ar.edu.utn.frsf.capitalhumano.model.Empresa;
import ar.edu.utn.frsf.capitalhumano.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    public List<ComunDTO.ItemSeleccion> obtenerEmpresasParaSelect() {
        List<Empresa> empresas = empresaRepository.findByFechaBajaIsNullOrderByNombreAsc();
        return empresaMapper.aItemsSeleccion(empresas);
    }
}
