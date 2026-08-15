package ar.edu.utn.frsf.capitalhumano.mapper;

import ar.edu.utn.frsf.capitalhumano.dto.*;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.enums.Genero;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoDocumento;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MappersTest {

    private CandidatoMapper candidatoMapper;
    private OpcionMapper opcionMapper;
    private PreguntaMapper preguntaMapper;
    private EmpresaMapper empresaMapper;
    private CompetenciaMapper competenciaMapper;
    private FactorMapper factorMapper;
    private PuestoMapper puestoMapper;
    private CuestionarioMapper cuestionarioMapper;
    private EvaluacionMapper evaluacionMapper;

    @BeforeEach
    void setUp() {
        candidatoMapper = Mappers.getMapper(CandidatoMapper.class);
        opcionMapper = Mappers.getMapper(OpcionMapper.class);
        preguntaMapper = Mappers.getMapper(PreguntaMapper.class);
        ReflectionTestUtils.setField(preguntaMapper, "opcionMapper", opcionMapper);

        empresaMapper = Mappers.getMapper(EmpresaMapper.class);
        competenciaMapper = Mappers.getMapper(CompetenciaMapper.class);
        factorMapper = Mappers.getMapper(FactorMapper.class);
        puestoMapper = Mappers.getMapper(PuestoMapper.class);
        cuestionarioMapper = Mappers.getMapper(CuestionarioMapper.class);
        evaluacionMapper = Mappers.getMapper(EvaluacionMapper.class);
    }

    @Test
    @DisplayName("CandidatoMapper - Mapeo de Candidato a CandidatoDTO.Resumen y Perfil")
    void testCandidatoMapper() {
        Candidato c = new Candidato();
        c.setId(10L);
        c.setNumeroCandidato(1001L);
        c.setNombre("Juan");
        c.setApellido("Perez");
        c.setNumeroDocumento("30111222");
        c.setTipoDocumento(TipoDocumento.DNI);
        c.setGenero(Genero.H);
        c.setFechaNacimiento(LocalDate.of(1995, 5, 20));

        CandidatoDTO.Resumen resumen = candidatoMapper.aResumen(c);
        assertNotNull(resumen);
        assertEquals(10L, resumen.id());
        assertEquals("Juan", resumen.nombre());
        assertEquals("Perez", resumen.apellido());
        assertEquals(1001L, resumen.numeroCandidato());

        CandidatoDTO.Perfil perfil = candidatoMapper.aPerfil(c, "30111222", "CANDIDATE");
        assertNotNull(perfil);
        assertEquals("30111222", perfil.nombreUsuario());
        assertEquals("CANDIDATE", perfil.rol());
        assertEquals("Juan", perfil.nombre());
        assertEquals("Perez", perfil.apellido());
    }

    @Test
    @DisplayName("PreguntaMapper & OpcionMapper - Mapeo de preguntas y opciones a Resumen y Detalle")
    void testPreguntaAndOpcionMapper() {
        Competencia comp = new Competencia();
        comp.setId(1L);
        comp.setNombre("Liderazgo");

        Factor factor = new Factor();
        factor.setId(2L);
        factor.setNombre("Toma de Decisiones");
        factor.setCompetencia(comp);

        Pregunta q = new Pregunta();
        q.setId(5L);
        q.setNombre("Pregunta de prueba");
        q.setTexto("¿Cómo actúa bajo presión?");
        q.setDescripcion("Evalúa templanza");
        q.setTipo(TipoPregunta.SINGLE_CHOICE);
        q.setFactor(factor);
        q.setFechaModificacion(LocalDateTime.now());

        Opcion op1 = new Opcion();
        op1.setId(101L);
        op1.setOrdenVisualizacion(1);
        op1.setPonderacion(10);
        op1.setTexto("Mantengo la calma");

        q.getOpciones().add(op1);

        PreguntaDTO.Resumen resumen = preguntaMapper.aResumen(q);
        assertNotNull(resumen);
        assertEquals(5L, resumen.id());
        assertEquals("Pregunta de prueba", resumen.nombrePregunta());
        assertEquals("Liderazgo", resumen.nombreCompetencia());
        assertEquals("Toma de Decisiones", resumen.nombreFactor());

        PreguntaDTO.Detalle detalle = preguntaMapper.aDetalle(q);
        assertNotNull(detalle);
        assertEquals(5L, detalle.id());
        assertEquals(2L, detalle.idFactor());
        assertEquals("Pregunta de prueba", detalle.nombre());
        assertEquals("¿Cómo actúa bajo presión?", detalle.texto());
        assertEquals(1, detalle.opciones().size());
        assertEquals(101L, detalle.opciones().get(0).id());
        assertEquals("Mantengo la calma", detalle.opciones().get(0).texto());
    }

    @Test
    @DisplayName("EmpresaMapper, CompetenciaMapper, FactorMapper")
    void testSelectMappers() {
        Empresa e = new Empresa();
        e.setId(1L);
        e.setNombre("Tech Corp");
        ComunDTO.ItemSeleccion eResp = empresaMapper.aItemSeleccion(e);
        assertEquals(1L, eResp.id());
        assertEquals("Tech Corp", eResp.nombre());

        Competencia c = new Competencia();
        c.setId(2L);
        c.setNombre("Trabajo en Equipo");
        ComunDTO.ItemSeleccion cResp = competenciaMapper.aItemSeleccion(c);
        assertEquals(2L, cResp.id());
        assertEquals("Trabajo en Equipo", cResp.nombre());

        Factor f = new Factor();
        f.setId(3L);
        f.setNombre("Comunicación");
        ComunDTO.ItemSeleccion fResp = factorMapper.aItemSeleccion(f);
        assertEquals(3L, fResp.id());
        assertEquals("Comunicación", fResp.nombre());
    }

    @Test
    @DisplayName("PuestoMapper - Mapeo de Puesto y PuestoCompetencia con contexto de válidos")
    void testPuestoMapper() {
        Empresa emp = new Empresa();
        emp.setNombre("Empresa ABC");

        Puesto puesto = new Puesto();
        puesto.setId(8L);
        puesto.setCodigo("DEV-01");
        puesto.setNombre("Senior Developer");
        puesto.setEmpresa(emp);

        Competencia c1 = new Competencia();
        c1.setId(10L);
        c1.setNombre("Java");

        PuestoCompetencia pc = new PuestoCompetencia();
        pc.setCompetencia(c1);
        pc.setPonderacionRequerida(8);
        puesto.setCompetencias(List.of(pc));

        Set<Long> validIds = Set.of(10L);
        PuestoDTO.Seleccion selectResp = puestoMapper.aSeleccion(puesto, validIds);
        assertNotNull(selectResp);
        assertEquals("Senior Developer", selectResp.nombre());
        assertEquals("Empresa ABC", selectResp.empresa());
        assertEquals(1, selectResp.competencias().size());
        assertTrue(selectResp.competencias().get(0).cumpleCondicion());

        PuestoDTO.Resumen resumenResp = puestoMapper.aResumen(puesto, 5, 3);
        assertNotNull(resumenResp);
        assertEquals("DEV-01", resumenResp.codigo());
        assertEquals(5, resumenResp.totalCandidatos());
        assertEquals(3, resumenResp.evaluacionesCompletadas());
    }

    @Test
    @DisplayName("CuestionarioMapper & EvaluacionMapper")
    void testCuestionarioAndEvaluacionMapper() {
        Candidato cand = new Candidato();
        cand.setId(1L);
        cand.setNombre("Laura");
        cand.setApellido("Lopez");
        cand.setNumeroCandidato(500L);
        cand.setNumeroDocumento("35123456");
        cand.setTipoDocumento(TipoDocumento.DNI);

        Evaluacion eval = new Evaluacion();
        eval.setId(100L);
        eval.setDuracion(45);

        Cuestionario q = new Cuestionario();
        q.setId(50L);
        q.setCandidato(cand);
        q.setEvaluacion(eval);
        q.setEstado(EstadoCuestionario.IN_PROGRESS);
        q.setFechaInicio(LocalDateTime.of(2026, 8, 15, 10, 0));
        q.setCantidadAccesos(2);

        CuestionarioDTO.Inicio initResp = cuestionarioMapper.aInicio(q, 3, 2);
        assertNotNull(initResp);
        assertEquals(50L, initResp.idCuestionario());
        assertEquals(3, initResp.totalBloques());
        assertEquals(2, initResp.bloqueActual());
        assertEquals(45, initResp.duracionMinutos());
        assertEquals("IN_PROGRESS", initResp.estado());

        EvaluacionDTO.ReporteCandidato repCand = cuestionarioMapper.aReporteCandidato(q);
        assertNotNull(repCand);
        assertEquals("Laura", repCand.nombre());
        assertEquals("Lopez", repCand.apellido());
        assertEquals("500", repCand.numeroCandidato());
        assertEquals("IN_PROGRESS", repCand.estado());

        EvaluacionDTO.ClaveGenerada claveResp = evaluacionMapper.aClaveGenerada(cand, "ABC12345");
        assertNotNull(claveResp);
        assertEquals("500", claveResp.numeroCandidato());
        assertEquals("Laura", claveResp.nombre());
        assertEquals("ABC12345", claveResp.claveAcceso());
    }
}
