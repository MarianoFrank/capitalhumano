package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import ar.edu.utn.frsf.capitalhumano.model.Opcion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OpcionMapper {

    PreguntaDTO.OpcionDetalle aDetalle(Opcion opcion);

    List<PreguntaDTO.OpcionDetalle> aDetalles(List<Opcion> opciones);
}
