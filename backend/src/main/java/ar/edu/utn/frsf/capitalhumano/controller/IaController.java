package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import ar.edu.utn.frsf.capitalhumano.service.IaGeneracionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<PreguntaDTO.IaRespuesta> generarPregunta(@Valid @RequestBody PreguntaDTO.IaPeticion peticion) {
        PreguntaDTO.IaRespuesta respuesta = iaGeneracionService.generarPregunta(peticion);
        return ResponseEntity.ok(respuesta);
    }
}
