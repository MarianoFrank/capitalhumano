package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.model.Factor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FactorMapper {

    ComunDTO.ItemSeleccion aItemSeleccion(Factor factor);

    List<ComunDTO.ItemSeleccion> aItemsSeleccion(List<Factor> factores);
}
