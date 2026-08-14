package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.response.CandidatoResumenResponse;
import ar.edu.utn.frsf.capitalhumano.service.CandidatoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @GetMapping
    public ResponseEntity<Page<CandidatoResumenResponse>> obtenerCandidatos(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "apellido", required = false) String apellido,
            @RequestParam(name = "numeroCandidato", required = false) Long numeroCandidato,
            Pageable pageable) {

        return ResponseEntity.ok(candidatoService.obtenerCandidatosPaginados(nombre, apellido, numeroCandidato, pageable));
    }

    @PostMapping("/importar")
    public ResponseEntity<List<CandidatoResumenResponse>> importarCandidatosCsv(
            @RequestParam("archivo") MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<CandidatoResumenResponse> candidatos = candidatoService.procesarCandidatosCsv(archivo);
        return ResponseEntity.ok(candidatos);
    }
}
