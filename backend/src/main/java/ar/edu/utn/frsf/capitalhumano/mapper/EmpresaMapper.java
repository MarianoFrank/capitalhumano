package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.model.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmpresaMapper {

    ComunDTO.ItemSeleccion aItemSeleccion(Empresa empresa);

    List<ComunDTO.ItemSeleccion> aItemsSeleccion(List<Empresa> empresas);
}
