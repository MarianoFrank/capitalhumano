package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.CandidatoDTO;
import ar.edu.utn.frsf.capitalhumano.service.CandidatoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Page<CandidatoDTO.Resumen>> obtenerPaginados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) Long numeroCandidato,
            @PageableDefault(size = 10, sort = "numeroCandidato", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CandidatoDTO.Resumen> candidatos = candidatoService.obtenerCandidatosPaginados(
                nombre, apellido, numeroCandidato, pageable);
        return ResponseEntity.ok(candidatos);
    }

    @PostMapping(value = "/cargar-csv", consumes = "multipart/form-data")
    public ResponseEntity<List<CandidatoDTO.Resumen>> importarCsv(
            @RequestParam(name = "archivo", required = false) MultipartFile archivo,
            @RequestParam(name = "file", required = false) MultipartFile file) {

        MultipartFile multipartFile = archivo != null ? archivo : file;
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV no puede estar vacío");
        }

        List<CandidatoDTO.Resumen> candidatosGuardados = candidatoService.procesarCandidatosCsv(multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidatosGuardados);
    }
}
