package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.service.CompetenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {

    private final CompetenciaService competenciaService;

    public CompetenciaController(CompetenciaService competenciaService) {
        this.competenciaService = competenciaService;
    }

    @GetMapping("/select")
    public ResponseEntity<List<ComunDTO.ItemSeleccion>> obtenerParaSelect() {
        List<ComunDTO.ItemSeleccion> competencias = competenciaService.obtenerCompetenciasParaSelect();
        return ResponseEntity.ok(competencias);
    }
}
