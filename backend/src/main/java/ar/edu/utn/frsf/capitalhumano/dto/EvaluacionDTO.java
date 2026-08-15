package ar.edu.utn.frsf.capitalhumano.dto;

import java.time.LocalDateTime;
import java.util.List;

public final class EvaluacionDTO {

    private EvaluacionDTO() {}

    // Petición: Generación de evaluación masiva para candidatos en un puesto
    public record Generar(
            Long idPuesto,
            List<Long> idsCandidatos
    ) {}

    // Respuesta: Clave y credenciales de acceso emitidas para cada candidato
    public record ClaveGenerada(
            String numeroCandidato,
            String nombre,
            String apellido,
            String claveAcceso
    ) {}

    // Respuesta: Resumen de evaluación para selectores de reportes
    public record Resumen(
            Long id,
            String descripcion
    ) {}

    // Respuesta: Reporte consolidado de orden de mérito
    public record ReporteOrdenMerito(
            String nombreEmpresa,
            String nombrePuesto,
            String emitidoPor,
            LocalDateTime fechaEmision,
            List<ReporteCandidato> candidatosAprobados,
            List<ReporteCandidato> candidatosRechazadosOIncompletos
    ) {}

    // Respuesta: Candidato individual dentro del orden de mérito
    public record ReporteCandidato(
            String nombre,
            String apellido,
            String tipoDocumento,
            String numeroDocumento,
            String numeroCandidato,
            String estado,
            Double puntaje,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Integer cantidadAccesos
    ) {}
}
