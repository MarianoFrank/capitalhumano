package ar.edu.utn.frsf.capitalhumano.dto;

import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import ar.edu.utn.frsf.capitalhumano.model.PuestoCompetencia;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class PuestoDTO {

    private PuestoDTO() {}

    // Respuesta: Resumen estadístico de puesto para reportes
    public record Resumen(
            Long id,
            String codigo,
            String nombrePuesto,
            String nombreEmpresa,
            int totalCandidatos,
            int evaluacionesCompletadas
    ) {
        public static Resumen from(Puesto puesto, int totalCandidatos, int evaluacionesCompletadas) {
            if (puesto == null) return null;
            String empresaNombre = (puesto.getEmpresa() != null) ? puesto.getEmpresa().getNombre() : null;
            return new Resumen(puesto.getId(), puesto.getCodigo(), puesto.getNombre(), empresaNombre, totalCandidatos, evaluacionesCompletadas);
        }
    }

    // Respuesta: Puesto con sus competencias requeridas para selección
    public record Seleccion(
            Long id,
            String nombre,
            String empresa,
            List<CompetenciaRequerida> competencias
    ) {
        public static Seleccion from(Puesto puesto, Set<Long> validCompetencyIds) {
            if (puesto == null) return null;
            String empresaNombre = (puesto.getEmpresa() != null) ? puesto.getEmpresa().getNombre() : null;
            List<CompetenciaRequerida> comps = (puesto.getCompetencias() != null)
                    ? puesto.getCompetencias().stream()
                            .map(pc -> CompetenciaRequerida.from(pc, validCompetencyIds))
                            .toList()
                    : Collections.emptyList();
            return new Seleccion(puesto.getId(), puesto.getNombre(), empresaNombre, comps);
        }

        public static List<Seleccion> fromList(List<Puesto> puestos, Set<Long> validCompetencyIds) {
            if (puestos == null) return Collections.emptyList();
            return puestos.stream()
                    .map(p -> from(p, validCompetencyIds))
                    .toList();
        }
    }

    // Respuesta: Detalle de ponderación de competencia en un puesto
    public record CompetenciaRequerida(
            String nombre,
            Integer ponderacionRequerida,
            boolean cumpleCondicion
    ) {
        public static CompetenciaRequerida from(PuestoCompetencia pc, Set<Long> validCompetencyIds) {
            if (pc == null) return null;
            String nombreComp = (pc.getCompetencia() != null) ? pc.getCompetencia().getNombre() : null;
            boolean cumple = validCompetencyIds != null && pc.getCompetencia() != null
                    && validCompetencyIds.contains(pc.getCompetencia().getId());
            return new CompetenciaRequerida(nombreComp, pc.getPonderacionRequerida(), cumple);
        }
    }
}
