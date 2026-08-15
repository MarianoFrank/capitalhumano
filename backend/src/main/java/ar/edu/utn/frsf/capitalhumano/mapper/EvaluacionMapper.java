package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EvaluacionMapper {

    @Mapping(target = "numeroCandidato", expression = "java(String.valueOf(candidato.getNumeroCandidato()))")
    @Mapping(target = "nombre", source = "candidato.nombre")
    @Mapping(target = "apellido", source = "candidato.apellido")
    EvaluacionDTO.ClaveGenerada aClaveGenerada(Candidato candidato, String claveAcceso);

    EvaluacionDTO.Resumen aResumen(Long id, String descripcion);
}
