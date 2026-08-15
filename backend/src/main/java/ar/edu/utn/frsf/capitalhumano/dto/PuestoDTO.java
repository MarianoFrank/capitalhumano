package ar.edu.utn.frsf.capitalhumano.dto;

import java.util.List;

public final class PuestoDTO {

    private PuestoDTO() {}

    // Respuesta: Puesto con sus competencias requeridas para selección
    public record Seleccion(
            Long id,
            String nombre,
            String empresa,
            List<CompetenciaRequerida> competencias
    ) {}

    // Respuesta: Detalle de ponderación de competencia en un puesto
    public record CompetenciaRequerida(
            String nombre,
            Integer ponderacionRequerida,
            boolean cumpleCondicion
    ) {}

    // Respuesta: Resumen estadístico de puesto para reportes
    public record Resumen(
            Long id,
            String codigo,
            String nombrePuesto,
            String nombreEmpresa,
            int totalCandidatos,
            int evaluacionesCompletadas
    ) {}
}
