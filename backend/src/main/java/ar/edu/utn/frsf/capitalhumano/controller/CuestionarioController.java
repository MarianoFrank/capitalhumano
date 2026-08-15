package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.CuestionarioDTO;
import ar.edu.utn.frsf.capitalhumano.service.CuestionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuestionarios")
public class CuestionarioController {

    private final CuestionarioService cuestionarioService;

    public CuestionarioController(CuestionarioService cuestionarioService) {
        this.cuestionarioService = cuestionarioService;
    }

    @PostMapping("/{idCuestionario}/iniciar")
    @PreAuthorize("@securityService.esPropietarioCuestionario(#idCuestionario)")
    public ResponseEntity<CuestionarioDTO.Inicio> iniciar(@PathVariable Long idCuestionario) {
        CuestionarioDTO.Inicio response = cuestionarioService.iniciarCuestionario(idCuestionario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idCuestionario}/bloques/{numeroBloque}")
    @PreAuthorize("@securityService.esPropietarioCuestionario(#idCuestionario)")
    public ResponseEntity<CuestionarioDTO.Bloque> obtenerBloque(
            @PathVariable Long idCuestionario,
            @PathVariable int numeroBloque) {
        CuestionarioDTO.Bloque response = cuestionarioService.obtenerBloque(idCuestionario, numeroBloque);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idCuestionario}/bloques/{numeroBloque}/responder")
    @PreAuthorize("@securityService.esPropietarioCuestionario(#idCuestionario)")
    public ResponseEntity<Void> guardarRespuestas(
            @PathVariable Long idCuestionario,
            @PathVariable int numeroBloque,
            @RequestBody Map<Long, List<Long>> respuestas) {
        cuestionarioService.guardarRespuestasBloque(idCuestionario, numeroBloque, respuestas);
        return ResponseEntity.ok().build();
    }
}
