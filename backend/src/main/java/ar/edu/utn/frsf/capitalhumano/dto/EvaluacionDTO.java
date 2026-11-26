package ar.edu.utn.frsf.capitalhumano.dto;

import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public final class EvaluacionDTO {

    private EvaluacionDTO() {}

    // Petición: Generación de evaluación masiva para candidatos en un puesto
    public record Generar(
            @NotNull(message = "El ID del puesto es obligatorio")
            Long idPuesto,
            @NotEmpty(message = "Debe seleccionar al menos un candidato")
            List<Long> idsCandidatos
    ) {}

    // Respuesta: Clave y credenciales de acceso emitidas para cada candidato
    public record ClaveGenerada(
            String numeroCandidato,
            String nombre,
            String apellido,
            String claveAcceso
    ) {
        public static ClaveGenerada from(Candidato candidato, String claveAcceso) {
            if (candidato == null) return null;
            String nro = candidato.getNumeroCandidato() != null ? String.valueOf(candidato.getNumeroCandidato()) : null;
            return new ClaveGenerada(nro, candidato.getNombre(), candidato.getApellido(), claveAcceso);
        }
    }

    // Respuesta: Resumen de evaluación para selectores de reportes
    public record Resumen(
            Long id,
            String descripcion
    ) {
        public static Resumen from(Long id, String descripcion) {
            return new Resumen(id, descripcion);
        }
    }

    // Respuesta: Reporte consolidado de orden de mérito
    public record ReporteOrdenMerito(
            String nombreEmpresa,
            String nombrePuesto,
            String emitidoPor,
            LocalDateTime fechaEmision,
            List<ReporteCandidato> candidatosAprobados,
            List<ReporteCandidato> candidatosRechazadosOIncompletos
    ) {
        public static ReporteOrdenMerito from(Puesto puesto, String consultor, LocalDateTime fechaEmision,
                                              List<ReporteCandidato> aprobados, List<ReporteCandidato> noAprobados) {
            String empresa = (puesto != null && puesto.getEmpresa() != null) ? puesto.getEmpresa().getNombre() : null;
            String puestoNombre = (puesto != null) ? puesto.getNombre() : null;
            return new ReporteOrdenMerito(empresa, puestoNombre, consultor, fechaEmision, aprobados, noAprobados);
        }
    }

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
    ) {
        public static ReporteCandidato from(Cuestionario cuestionario) {
            if (cuestionario == null) return null;
            Candidato cand = cuestionario.getCandidato();
            boolean completado = cuestionario.getEstado() == EstadoCuestionario.COMPLETED;

            String nombre = cand != null ? cand.getNombre() : null;
            String apellido = cand != null ? cand.getApellido() : null;
            String tipoDoc = (cand != null && cand.getTipoDocumento() != null) ? cand.getTipoDocumento().name() : null;
            String numDoc = cand != null ? cand.getNumeroDocumento() : null;
            String numCand = (cand != null && cand.getNumeroCandidato() != null) ? String.valueOf(cand.getNumeroCandidato()) : null;
            String estado = (cuestionario.getEstado() != null) ? cuestionario.getEstado().name() : null;
            Double puntaje = completado ? cuestionario.getPuntajeTotal() : null;
            LocalDateTime fechaFin = completado ? cuestionario.getFechaFin() : cuestionario.getUltimoAcceso();

            return new ReporteCandidato(
                    nombre,
                    apellido,
                    tipoDoc,
                    numDoc,
                    numCand,
                    estado,
                    puntaje,
                    cuestionario.getFechaInicio(),
                    fechaFin,
                    cuestionario.getCantidadAccesos()
            );
        }
    }
}
