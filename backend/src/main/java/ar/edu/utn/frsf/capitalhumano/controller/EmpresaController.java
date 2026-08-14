package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.response.EmpresaSelectResponse;
import ar.edu.utn.frsf.capitalhumano.repository.EmpresaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@PreAuthorize("hasRole('CONSULTANT')")
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    public EmpresaController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/seleccion")
    public ResponseEntity<List<EmpresaSelectResponse>> obtenerEmpresasParaSelect() {
        return ResponseEntity.ok(empresaRepository.findAllForSelect());
    }
}
