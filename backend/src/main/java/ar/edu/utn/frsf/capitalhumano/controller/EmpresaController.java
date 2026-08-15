package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/select")
    public ResponseEntity<List<ComunDTO.ItemSeleccion>> obtenerParaSelect() {
        List<ComunDTO.ItemSeleccion> empresas = empresaService.obtenerEmpresasParaSelect();
        return ResponseEntity.ok(empresas);
    }
}
