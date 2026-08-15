package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import ar.edu.utn.frsf.capitalhumano.model.Pregunta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {OpcionMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PreguntaMapper {

    @Mapping(target = "nombreCompetencia", source = "factor.competencia.nombre")
    @Mapping(target = "nombreFactor", source = "factor.nombre")
    @Mapping(target = "nombrePregunta", source = "nombre")
    PreguntaDTO.Resumen aResumen(Pregunta pregunta);

    default Page<PreguntaDTO.Resumen> aPaginaResumen(Page<Pregunta> page) {
        return page.map(this::aResumen);
    }

    @Mapping(target = "idFactor", source = "factor.id")
    PreguntaDTO.Detalle aDetalle(Pregunta pregunta);
}
