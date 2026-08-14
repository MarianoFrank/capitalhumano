package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.request.GenerarEvaluacionRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.ClaveEvaluacionResponse;
import ar.edu.utn.frsf.capitalhumano.service.EvaluacionService;
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
    public ResponseEntity<List<ClaveEvaluacionResponse>> generarEvaluacion(@RequestBody GenerarEvaluacionRequest peticion) {
        List<ClaveEvaluacionResponse> respuesta = evaluacionService.generarEvaluacion(peticion);
        return ResponseEntity.ok(respuesta);
    }
}
