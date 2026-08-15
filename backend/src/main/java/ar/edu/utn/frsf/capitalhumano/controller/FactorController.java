package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.service.FactorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/factores")
public class FactorController {

    private final FactorService factorService;

    public FactorController(FactorService factorService) {
        this.factorService = factorService;
    }

    @GetMapping("/select")
    public ResponseEntity<List<ComunDTO.ItemSeleccion>> obtenerParaSelect(
            @RequestParam(name = "idCompetencia", required = false) Long idCompetencia) {
        List<ComunDTO.ItemSeleccion> factores = factorService.obtenerFactoresParaSelect(idCompetencia);
        return ResponseEntity.ok(factores);
    }
}
