package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.CandidatoDTO;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidatoMapper {

    CandidatoDTO.Resumen aResumen(Candidato candidato);

    List<CandidatoDTO.Resumen> aResumenes(List<Candidato> candidatos);

    default Page<CandidatoDTO.Resumen> aPaginaResumen(Page<Candidato> page) {
        return page.map(this::aResumen);
    }

    @Mapping(target = "nombreUsuario", source = "nombrePrincipal")
    @Mapping(target = "nombre", source = "candidato.nombre")
    @Mapping(target = "apellido", source = "candidato.apellido")
    CandidatoDTO.Perfil aPerfil(Candidato candidato, String nombrePrincipal, String rol);
}
