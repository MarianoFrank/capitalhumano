package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.dto.PuestoDTO;
import ar.edu.utn.frsf.capitalhumano.service.ReporteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/puestos")
    public ResponseEntity<Page<PuestoDTO.Resumen>> obtenerPuestosPaginados(
            @RequestParam(name = "idEmpresa", required = false) Long idEmpresa,
            @RequestParam(name = "nombrePuesto", required = false) String nombrePuesto,
            @RequestParam(name = "codigo", required = false) String codigo,
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<PuestoDTO.Resumen> puestos = reporteService.obtenerPuestosParaReporte(idEmpresa, nombrePuesto, codigo, pageable);
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/orden-merito/{idPuesto}")
    public ResponseEntity<EvaluacionDTO.ReporteOrdenMerito> obtenerOrdenMerito(
            @PathVariable Long idPuesto,
            @RequestParam(name = "idEvaluacion", required = false) Long idEvaluacion) {
        EvaluacionDTO.ReporteOrdenMerito reporte = reporteService.generarOrdenMerito(idPuesto, idEvaluacion);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/puestos/{idPuesto}/evaluaciones")
    public ResponseEntity<List<EvaluacionDTO.Resumen>> obtenerEvaluacionesPorPuesto(@PathVariable Long idPuesto) {
        List<EvaluacionDTO.Resumen> evaluaciones = reporteService.obtenerResumenEvaluacionesPorPuesto(idPuesto);
        return ResponseEntity.ok(evaluaciones);
    }
}
