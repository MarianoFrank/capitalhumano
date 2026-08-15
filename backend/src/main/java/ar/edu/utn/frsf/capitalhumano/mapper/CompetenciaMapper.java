package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.model.Competencia;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompetenciaMapper {

    ComunDTO.ItemSeleccion aItemSeleccion(Competencia competencia);

    List<ComunDTO.ItemSeleccion> aItemsSeleccion(List<Competencia> competencias);
}
