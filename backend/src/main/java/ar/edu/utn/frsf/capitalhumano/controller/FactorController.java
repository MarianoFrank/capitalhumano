package ar.edu.utn.frsf.capitalhumano.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse;
import ar.edu.utn.frsf.capitalhumano.service.FactorService;

@RestController
@RequestMapping("/api/factores")
public class FactorController {

    private final FactorService factorService;

    public FactorController(FactorService factorService) {
        this.factorService = factorService;
    }

    @GetMapping("/seleccion")
    public ResponseEntity<List<SelectItemResponse>> obtenerFactoresParaSelect(
            @RequestParam(name = "idCompetencia", required = false) Long idCompetencia) {
        List<SelectItemResponse> factores = factorService.obtenerFactoresParaSelect(idCompetencia);
        return ResponseEntity.ok(factores);
    }
}
