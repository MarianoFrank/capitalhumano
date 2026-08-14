package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.response.EvaluacionResumenResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.ReporteOrdenMeritoResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PuestoResumenResponse;
import ar.edu.utn.frsf.capitalhumano.service.ReporteService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasRole('CONSULTANT')")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // Endpoint: GET /api/reportes/orden-merito/puestos/8?idEvaluacion=15
    @GetMapping("/orden-merito/puestos/{idPuesto}")
    public ResponseEntity<ReporteOrdenMeritoResponse> obtenerOrdenMerito(
            @PathVariable Long idPuesto,
            @RequestParam(name = "idEvaluacion", required = false) Long idEvaluacion) {

        ReporteOrdenMeritoResponse respuesta = reporteService.generarOrdenMerito(idPuesto, idEvaluacion);
        return ResponseEntity.ok(respuesta);
    }

    // Endpoint para la grilla paginada y filtrada
    @GetMapping("/orden-merito/puestos")
    public ResponseEntity<Page<PuestoResumenResponse>> obtenerPuestosParaOrdenMerito(
            @RequestParam(name = "idEmpresa", required = false) Long idEmpresa,
            @RequestParam(name = "nombrePuesto", required = false) String nombrePuesto,
            @RequestParam(name = "codigo", required = false) String codigo,
            Pageable pageable) {

        Page<PuestoResumenResponse> respuesta = reporteService.obtenerPuestosParaReporte(idEmpresa, nombrePuesto, codigo, pageable);
        return ResponseEntity.ok(respuesta);
    }

    // Endpoint auxiliar para llenar el dropdown del modal
    @GetMapping("/orden-merito/puestos/{idPuesto}/evaluaciones")
    public ResponseEntity<List<EvaluacionResumenResponse>> obtenerEvaluacionesPorPuesto(@PathVariable Long idPuesto) {
        List<EvaluacionResumenResponse> respuesta = reporteService.obtenerResumenEvaluacionesPorPuesto(idPuesto);
        return ResponseEntity.ok(respuesta);
    }
}
