package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.service.EvaluacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping("/generar")
    public ResponseEntity<List<EvaluacionDTO.ClaveGenerada>> generar(@Valid @RequestBody EvaluacionDTO.Generar peticion) {
        List<EvaluacionDTO.ClaveGenerada> claves = evaluacionService.generarEvaluacion(peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(claves);
    }
}
