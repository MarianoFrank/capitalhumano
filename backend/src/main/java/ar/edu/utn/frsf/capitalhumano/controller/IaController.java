package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.request.GenerarPreguntaRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.GenerarPreguntaResponse;
import ar.edu.utn.frsf.capitalhumano.service.IaGeneracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final IaGeneracionService iaGeneracionService;

    public IaController(IaGeneracionService iaGeneracionService) {
        this.iaGeneracionService = iaGeneracionService;
    }

    @PostMapping("/generar-pregunta")
    public ResponseEntity<GenerarPreguntaResponse> generarPregunta(@RequestBody GenerarPreguntaRequest peticion) {
        return ResponseEntity.ok(iaGeneracionService.generarPregunta(peticion));
    }
}
