package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.response.BloqueResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.IniciarCuestionarioResponse;
import ar.edu.utn.frsf.capitalhumano.service.CuestionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuestionarios")
public class CuestionarioController {

    private final CuestionarioService cuestionarioService;

    public CuestionarioController(CuestionarioService cuestionarioService) {
        this.cuestionarioService = cuestionarioService;
    }

    @PostMapping("/{id}/iniciar")
    @PreAuthorize("@securityService.esPropietarioCuestionario(#id)")
    public ResponseEntity<IniciarCuestionarioResponse> iniciarCuestionario(@PathVariable Long id) {
        IniciarCuestionarioResponse cuestionario = cuestionarioService.iniciarCuestionario(id);
        return ResponseEntity.ok(cuestionario);
    }

    @GetMapping("/{id}/bloques/{numeroBloque}")
    @PreAuthorize("@securityService.esPropietarioCuestionario(#id)")
    public ResponseEntity<BloqueResponse> obtenerBloque(@PathVariable Long id, @PathVariable int numeroBloque) {
        BloqueResponse bloque = cuestionarioService.obtenerBloquePorNumero(id, numeroBloque);
        return ResponseEntity.ok(bloque);
    }

    @PostMapping("/{id}/bloques/{numeroBloque}")
    @PreAuthorize("@securityService.esPropietarioCuestionario(#id)")
    public ResponseEntity<?> enviarBloque(
            @PathVariable Long id,
            @PathVariable int numeroBloque,
            @RequestBody Map<String, List<Long>> respuestasCrudas) {

        Map<Long, List<Long>> respuestas = new HashMap<>();
        respuestasCrudas.forEach((key, value) -> respuestas.put(Long.valueOf(key), value));

        cuestionarioService.enviarRespuestasBloque(id, numeroBloque, respuestas);
        return ResponseEntity.ok(Map.of("message", "Bloque guardado con éxito"));
    }
}
