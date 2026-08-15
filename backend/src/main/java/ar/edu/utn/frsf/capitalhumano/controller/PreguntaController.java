package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import ar.edu.utn.frsf.capitalhumano.service.IaGeneracionService;
import ar.edu.utn.frsf.capitalhumano.service.PreguntaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final IaGeneracionService iaGeneracionService;

    public PreguntaController(PreguntaService preguntaService, IaGeneracionService iaGeneracionService) {
        this.preguntaService = preguntaService;
        this.iaGeneracionService = iaGeneracionService;
    }

    @GetMapping
    public ResponseEntity<Page<PreguntaDTO.Resumen>> obtenerPaginados(
            @RequestParam(name = "idCompetencia", required = false) Long idCompetencia,
            @RequestParam(name = "idFactor", required = false) Long idFactor,
            @RequestParam(name = "nombrePregunta", required = false) String nombrePregunta,
            @PageableDefault(size = 10, sort = "fechaModificacion", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PreguntaDTO.Resumen> preguntas = preguntaService.obtenerTodasLasPreguntasResumen(
                idCompetencia, idFactor, nombrePregunta, pageable);
        return ResponseEntity.ok(preguntas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreguntaDTO.Detalle> obtenerPorId(@PathVariable Long id) {
        PreguntaDTO.Detalle pregunta = preguntaService.obtenerPreguntaPorId(id);
        return ResponseEntity.ok(pregunta);
    }

    @PostMapping
    public ResponseEntity<PreguntaDTO.Detalle> crear(@Valid @RequestBody PreguntaDTO.Guardar peticion) {
        var creada = preguntaService.crearPregunta(peticion);
        PreguntaDTO.Detalle detalle = preguntaService.obtenerPreguntaPorId(creada.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(detalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreguntaDTO.Detalle> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PreguntaDTO.Guardar peticion) {
        var actualizada = preguntaService.actualizarPregunta(id, peticion);
        PreguntaDTO.Detalle detalle = preguntaService.obtenerPreguntaPorId(actualizada.getId());
        return ResponseEntity.ok(detalle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        preguntaService.eliminarPregunta(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generar-ia")
    public ResponseEntity<PreguntaDTO.IaRespuesta> generarPreguntaIa(@Valid @RequestBody PreguntaDTO.IaPeticion peticion) {
        PreguntaDTO.IaRespuesta respuesta = iaGeneracionService.generarPregunta(peticion);
        return ResponseEntity.ok(respuesta);
    }
}
