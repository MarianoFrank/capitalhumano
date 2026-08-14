package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.response.PuestoSelectResponse;
import ar.edu.utn.frsf.capitalhumano.service.PuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
public class PuestoController {

    private final PuestoService puestoService;

    public PuestoController(PuestoService puestoService) {
        this.puestoService = puestoService;
    }

    @GetMapping("/seleccion")
    public ResponseEntity<List<PuestoSelectResponse>> obtenerPuestosParaSelect() {
        return ResponseEntity.ok(puestoService.obtenerPuestosParaSelect());
    }
}
