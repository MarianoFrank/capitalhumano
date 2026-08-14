package ar.edu.utn.frsf.capitalhumano.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frsf.capitalhumano.dto.response.SelectItemResponse;
import ar.edu.utn.frsf.capitalhumano.service.CompetenciaService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {

    private final CompetenciaService competenciaService;

    public CompetenciaController(CompetenciaService competenciaService) {
        this.competenciaService = competenciaService;
    }

    @GetMapping("/seleccion")
    public ResponseEntity<List<SelectItemResponse>> obtenerCompetenciasParaSelect() {
        List<SelectItemResponse> competencias = competenciaService.obtenerCompetenciasParaSelect();
        return ResponseEntity.ok(competencias);
    }
}
