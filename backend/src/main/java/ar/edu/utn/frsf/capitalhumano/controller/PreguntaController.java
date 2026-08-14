package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.request.PreguntaRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.PreguntaDetalleResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PreguntaResumenResponse;
import ar.edu.utn.frsf.capitalhumano.service.PreguntaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preguntas")
@Validated
public class PreguntaController {

    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    // Endpoint Liviano (para la tabla principal)
    @GetMapping
    public ResponseEntity<Page<PreguntaResumenResponse>> obtenerPreguntas(
            @RequestParam(name = "idCompetencia", required = false) Long idCompetencia,
            @RequestParam(name = "idFactor", required = false) Long idFactor,
            @RequestParam(name = "nombrePregunta", required = false) String nombrePregunta,
            Pageable pageable) {

        Page<PreguntaResumenResponse> preguntasPage = preguntaService.obtenerTodasLasPreguntasResumen(
                idCompetencia, idFactor, nombrePregunta, pageable);
        return ResponseEntity.ok(preguntasPage);
    }

    // Endpoint Detallado (para popular el formulario al modificar)
    @GetMapping("/{id}")
    public ResponseEntity<PreguntaDetalleResponse> obtenerPreguntaPorId(
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {
        PreguntaDetalleResponse preguntaDetalle = preguntaService.obtenerPreguntaPorId(id);
        return ResponseEntity.ok(preguntaDetalle);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPregunta(@Valid @RequestBody PreguntaRequest peticionDTO) {
        preguntaService.crearPregunta(peticionDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Pregunta creada correctamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarPregunta(
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id,
            @Valid @RequestBody PreguntaRequest peticionDTO) {
        preguntaService.actualizarPregunta(id, peticionDTO);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Pregunta actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarPregunta(
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {

        preguntaService.eliminarPregunta(id);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Pregunta dada de baja correctamente"));
    }
}
