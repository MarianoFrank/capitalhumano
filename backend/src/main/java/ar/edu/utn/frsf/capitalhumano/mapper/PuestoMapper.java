package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.PuestoDTO;
import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import ar.edu.utn.frsf.capitalhumano.model.PuestoCompetencia;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PuestoMapper {

    @Mapping(target = "id", source = "puesto.id")
    @Mapping(target = "codigo", source = "puesto.codigo")
    @Mapping(target = "nombrePuesto", source = "puesto.nombre")
    @Mapping(target = "nombreEmpresa", source = "puesto.empresa.nombre")
    PuestoDTO.Resumen aResumen(Puesto puesto, int totalCandidatos, int evaluacionesCompletadas);

    @Mapping(target = "nombre", source = "pc.competencia.nombre")
    @Mapping(target = "cumpleCondicion", expression = "java(validCompetencyIds != null && pc.getCompetencia() != null && validCompetencyIds.contains(pc.getCompetencia().getId()))")
    PuestoDTO.CompetenciaRequerida aCompetenciaRequerida(PuestoCompetencia pc, @Context Set<Long> validCompetencyIds);

    List<PuestoDTO.CompetenciaRequerida> aCompetenciasRequeridas(List<PuestoCompetencia> competencias, @Context Set<Long> validCompetencyIds);

    @Mapping(target = "empresa", source = "empresa.nombre")
    @Mapping(target = "competencias", expression = "java(aCompetenciasRequeridas(puesto.getCompetencias(), validCompetencyIds))")
    PuestoDTO.Seleccion aSeleccion(Puesto puesto, @Context Set<Long> validCompetencyIds);

    default List<PuestoDTO.Seleccion> aSelecciones(List<Puesto> puestos, Set<Long> validCompetencyIds) {
        if (puestos == null) {
            return Collections.emptyList();
        }
        return puestos.stream()
                .map(p -> aSeleccion(p, validCompetencyIds))
                .toList();
    }
}
