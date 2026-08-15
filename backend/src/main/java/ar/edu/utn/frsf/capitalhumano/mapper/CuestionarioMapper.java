package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.CuestionarioDTO;
import ar.edu.utn.frsf.capitalhumano.dto.EvaluacionDTO;
import ar.edu.utn.frsf.capitalhumano.model.Bloque;
import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.ItemOpcion;
import ar.edu.utn.frsf.capitalhumano.model.ItemPregunta;
import ar.edu.utn.frsf.capitalhumano.model.Puesto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CuestionarioMapper {

    @Mapping(target = "idCuestionario", source = "cuestionario.id")
    @Mapping(target = "duracionMinutos", source = "cuestionario.evaluacion.duracion")
    @Mapping(target = "estado", expression = "java(cuestionario.getEstado() != null ? cuestionario.getEstado().name() : null)")
    @Mapping(target = "fechaInicio", source = "cuestionario.fechaInicio")
    CuestionarioDTO.Inicio aInicio(Cuestionario cuestionario, int totalBloques, int bloqueActual);

    @Mapping(target = "ordenVisualizacion", source = "opcion.ordenVisualizacion")
    @Mapping(target = "texto", source = "opcion.texto")
    CuestionarioDTO.ItemOpcion aItemOpcion(ItemOpcion itemOpcion);

    List<CuestionarioDTO.ItemOpcion> aItemsOpcion(List<ItemOpcion> itemOpciones);

    @Mapping(target = "texto", source = "pregunta.texto")
    @Mapping(target = "esMultiple", expression = "java(item.getPregunta() != null && item.getPregunta().getTipo() != null && item.getPregunta().getTipo().name().toUpperCase().contains(\"MULTIPLE\"))")
    CuestionarioDTO.ItemPregunta aItemPregunta(ItemPregunta item);

    List<CuestionarioDTO.ItemPregunta> aItemsPregunta(List<ItemPregunta> items);

    CuestionarioDTO.Bloque aBloque(Bloque bloque);

    @Mapping(target = "nombre", source = "candidato.nombre")
    @Mapping(target = "apellido", source = "candidato.apellido")
    @Mapping(target = "tipoDocumento", expression = "java(cuestionario.getCandidato() != null && cuestionario.getCandidato().getTipoDocumento() != null ? cuestionario.getCandidato().getTipoDocumento().name() : null)")
    @Mapping(target = "numeroDocumento", source = "candidato.numeroDocumento")
    @Mapping(target = "numeroCandidato", expression = "java(cuestionario.getCandidato() != null ? String.valueOf(cuestionario.getCandidato().getNumeroCandidato()) : null)")
    @Mapping(target = "estado", expression = "java(cuestionario.getEstado() != null ? cuestionario.getEstado().name() : null)")
    @Mapping(target = "puntaje", expression = "java(cuestionario.getEstado() == ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario.COMPLETED ? cuestionario.getPuntajeTotal() : null)")
    @Mapping(target = "fechaFin", expression = "java(cuestionario.getEstado() == ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario.COMPLETED ? cuestionario.getFechaFin() : cuestionario.getUltimoAcceso())")
    EvaluacionDTO.ReporteCandidato aReporteCandidato(Cuestionario cuestionario);

    @Mapping(target = "nombreEmpresa", source = "puesto.empresa.nombre")
    @Mapping(target = "nombrePuesto", source = "puesto.nombre")
    @Mapping(target = "emitidoPor", source = "consultor")
    @Mapping(target = "candidatosAprobados", source = "aprobados")
    @Mapping(target = "candidatosRechazadosOIncompletos", source = "noAprobados")
    EvaluacionDTO.ReporteOrdenMerito aReporteOrdenMerito(Puesto puesto, String consultor, LocalDateTime fechaEmision, List<EvaluacionDTO.ReporteCandidato> aprobados, List<EvaluacionDTO.ReporteCandidato> noAprobados);
}
