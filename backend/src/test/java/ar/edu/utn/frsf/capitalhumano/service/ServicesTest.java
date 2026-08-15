package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.*;
import ar.edu.utn.frsf.capitalhumano.mapper.*;
import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import ar.edu.utn.frsf.capitalhumano.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicesTest {

    @Mock
    private CandidatoRepository candidatoRepository;
    @Mock
    private PreguntaRepository preguntaRepository;
    @Mock
    private FactorRepository factorRepository;
    @Mock
    private PuestoRepository puestoRepository;
    @Mock
    private CompetenciaRepository competenciaRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private EvaluacionRepository evaluacionRepository;
    @Mock
    private CuestionarioRepository cuestionarioRepository;
    @Mock
    private PuntajeAsyncService puntajeAsyncService;

    private CandidatoMapper candidatoMapper;
    private PreguntaMapper preguntaMapper;
    private OpcionMapper opcionMapper;
    private EmpresaMapper empresaMapper;
    private CompetenciaMapper competenciaMapper;
    private FactorMapper factorMapper;
    private PuestoMapper puestoMapper;
    private CuestionarioMapper cuestionarioMapper;
    private EvaluacionMapper evaluacionMapper;

    private CandidatoService candidatoService;
    private PreguntaService preguntaService;
    private PuestoService puestoService;
    private EmpresaService empresaService;
    private CompetenciaService competenciaService;
    private FactorService factorService;
    private ReporteService reporteService;

    @BeforeEach
    void setUp() {
        candidatoMapper = Mappers.getMapper(CandidatoMapper.class);
        opcionMapper = Mappers.getMapper(OpcionMapper.class);
        preguntaMapper = Mappers.getMapper(PreguntaMapper.class);
        empresaMapper = Mappers.getMapper(EmpresaMapper.class);
        competenciaMapper = Mappers.getMapper(CompetenciaMapper.class);
        factorMapper = Mappers.getMapper(FactorMapper.class);
        puestoMapper = Mappers.getMapper(PuestoMapper.class);
        cuestionarioMapper = Mappers.getMapper(CuestionarioMapper.class);
        evaluacionMapper = Mappers.getMapper(EvaluacionMapper.class);

        candidatoService = new CandidatoService(candidatoRepository, candidatoMapper);
        preguntaService = new PreguntaService(preguntaRepository, factorRepository, preguntaMapper);
        puestoService = new PuestoService(puestoRepository, competenciaRepository, puestoMapper);
        empresaService = new EmpresaService(empresaRepository, empresaMapper);
        competenciaService = new CompetenciaService(competenciaRepository, competenciaMapper);
        factorService = new FactorService(factorRepository, factorMapper);
        reporteService = new ReporteService(cuestionarioRepository, puestoRepository, evaluacionRepository, puntajeAsyncService, puestoMapper, cuestionarioMapper, evaluacionMapper);
    }

    @Test
    @DisplayName("CandidatoService - obtenerCandidatosPaginados con Specification")
    void testCandidatoServicePaginado() {
        Candidato c = new Candidato();
        c.setId(1L);
        c.setNombre("Pedro");
        c.setApellido("Picapiedra");
        c.setNumeroCandidato(100L);

        Page<Candidato> entityPage = new PageImpl<>(List.of(c));
        when(candidatoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);

        Page<CandidatoDTO.Resumen> result = candidatoService.obtenerCandidatosPaginados("Pedro", null, null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Pedro", result.getContent().get(0).nombre());
        assertEquals("Picapiedra", result.getContent().get(0).apellido());
        verify(candidatoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("PreguntaService - obtenerTodasLasPreguntasResumen con Specification")
    void testPreguntaServicePaginado() {
        Competencia comp = new Competencia();
        comp.setNombre("Comunicación");

        Factor fact = new Factor();
        fact.setNombre("Oral");
        fact.setCompetencia(comp);

        Pregunta q = new Pregunta();
        q.setId(10L);
        q.setNombre("Pregunta 1");
        q.setFactor(fact);

        Page<Pregunta> entityPage = new PageImpl<>(List.of(q));
        when(preguntaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);

        Page<PreguntaDTO.Resumen> result = preguntaService.obtenerTodasLasPreguntasResumen(1L, 2L, "Pregunta", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Pregunta 1", result.getContent().get(0).nombrePregunta());
        assertEquals("Comunicación", result.getContent().get(0).nombreCompetencia());
        assertEquals("Oral", result.getContent().get(0).nombreFactor());
    }

    @Test
    @DisplayName("PreguntaService - crearPregunta con construcción manual de Entidad")
    void testCrearPregunta() {
        Factor factor = new Factor();
        factor.setId(5L);
        when(factorRepository.findById(5L)).thenReturn(Optional.of(factor));
        when(preguntaRepository.save(any(Pregunta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PreguntaDTO.Guardar req = new PreguntaDTO.Guardar(
                5L, "Nombre", "Texto", "Desc", TipoPregunta.SINGLE_CHOICE,
                List.of(new PreguntaDTO.OpcionGuardar(1, 10, "Opción 1"))
        );

        Pregunta creada = preguntaService.crearPregunta(req);
        assertNotNull(creada);
        assertEquals("Nombre", creada.getNombre());
        assertEquals("Texto", creada.getTexto());
        assertEquals(TipoPregunta.SINGLE_CHOICE, creada.getTipo());
        assertEquals(1, creada.getOpciones().size());
        assertEquals(1, creada.getOpciones().get(0).getOrdenVisualizacion());
        assertEquals(10, creada.getOpciones().get(0).getPonderacion());
        assertEquals("Opción 1", creada.getOpciones().get(0).getTexto());
        assertEquals(factor, creada.getFactor());
    }

    @Test
    @DisplayName("EmpresaService - obtenerEmpresasParaSelect")
    void testEmpresaService() {
        Empresa e = new Empresa();
        e.setId(1L);
        e.setNombre("Empresa 1");
        when(empresaRepository.findByFechaBajaIsNullOrderByNombreAsc()).thenReturn(List.of(e));

        List<ComunDTO.ItemSeleccion> res = empresaService.obtenerEmpresasParaSelect();
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Empresa 1", res.get(0).nombre());
    }

    @Test
    @DisplayName("CompetenciaService - obtenerCompetenciasParaSelect")
    void testCompetenciaService() {
        Competencia c = new Competencia();
        c.setId(1L);
        c.setNombre("Liderazgo");
        when(competenciaRepository.findByFechaBajaIsNullOrderByNombreAsc()).thenReturn(List.of(c));

        List<ComunDTO.ItemSeleccion> res = competenciaService.obtenerCompetenciasParaSelect();
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Liderazgo", res.get(0).nombre());
    }

    @Test
    @DisplayName("FactorService - obtenerFactoresParaSelect")
    void testFactorService() {
        Factor f = new Factor();
        f.setId(1L);
        f.setNombre("Factor 1");
        when(factorRepository.findByCompetenciaIdAndFechaBajaIsNullOrderByNombreAsc(2L)).thenReturn(List.of(f));

        List<ComunDTO.ItemSeleccion> res = factorService.obtenerFactoresParaSelect(2L);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Factor 1", res.get(0).nombre());
    }

    @Test
    @DisplayName("ReporteService - obtenerPuestosParaReporte con Specification")
    void testReporteServicePuestos() {
        Empresa emp = new Empresa();
        emp.setNombre("Mi Empresa");

        Puesto p = new Puesto();
        p.setId(1L);
        p.setCodigo("COD1");
        p.setNombre("Puesto 1");
        p.setEmpresa(emp);

        Page<Puesto> puestosPage = new PageImpl<>(List.of(p));
        when(puestoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(puestosPage);
        when(cuestionarioRepository.findByEvaluacionPuestoId(1L)).thenReturn(List.of());

        Page<PuestoDTO.Resumen> res = reporteService.obtenerPuestosParaReporte(1L, "Puesto", "COD", PageRequest.of(0, 10));

        assertNotNull(res);
        assertEquals(1, res.getContent().size());
        assertEquals("COD1", res.getContent().get(0).codigo());
        assertEquals("Puesto 1", res.getContent().get(0).nombrePuesto());
        assertEquals("Mi Empresa", res.getContent().get(0).nombreEmpresa());
    }

    @Test
    @DisplayName("ReporteService - generarOrdenMerito valida ponderación requerida por competencia")
    void testGenerarOrdenMeritoConValidacionPonderacionMinima() {
        // Puesto con 2 competencias requeridas
        Puesto puesto = new Puesto();
        puesto.setId(10L);
        puesto.setNombre("Desarrollador");

        Competencia comp1 = new Competencia();
        comp1.setId(1L);
        comp1.setNombre("Programación");

        Competencia comp2 = new Competencia();
        comp2.setId(2L);
        comp2.setNombre("Liderazgo");

        PuestoCompetencia pc1 = new PuestoCompetencia();
        pc1.setPuesto(puesto);
        pc1.setCompetencia(comp1);
        pc1.setPonderacionRequerida(7);

        PuestoCompetencia pc2 = new PuestoCompetencia();
        pc2.setPuesto(puesto);
        pc2.setCompetencia(comp2);
        pc2.setPonderacionRequerida(8);

        puesto.setCompetencias(List.of(pc1, pc2));

        // Candidato 1: Completa y supera ambas ponderaciones (Prog: 8.0 >= 7, Lid: 8.5 >= 8) -> APROBADO
        Candidato cand1 = new Candidato();
        cand1.setId(101L);
        cand1.setNombre("Lucas");
        cand1.setApellido("Aprobado");
        cand1.setNumeroCandidato(1001L);

        Cuestionario q1 = new Cuestionario();
        q1.setId(1L);
        q1.setCandidato(cand1);
        q1.setEstado(ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario.COMPLETED);
        q1.setPuntajeTotal(8.25);

        PuntajeCompetencia pComp1_q1 = new PuntajeCompetencia();
        pComp1_q1.setCompetencia(comp1);
        pComp1_q1.setPuntaje(8.0);

        PuntajeCompetencia pComp2_q1 = new PuntajeCompetencia();
        pComp2_q1.setCompetencia(comp2);
        pComp2_q1.setPuntaje(8.5);

        q1.setPuntajesCompetencias(List.of(pComp1_q1, pComp2_q1));

        // Candidato 2: Completa pero NO supera Liderazgo (Prog: 9.0 >= 7, Lid: 6.0 < 8) -> NO APROBADO
        Candidato cand2 = new Candidato();
        cand2.setId(102L);
        cand2.setNombre("Marta");
        cand2.setApellido("Desaprobada");
        cand2.setNumeroCandidato(1002L);

        Cuestionario q2 = new Cuestionario();
        q2.setId(2L);
        q2.setCandidato(cand2);
        q2.setEstado(ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario.COMPLETED);
        q2.setPuntajeTotal(7.5);

        PuntajeCompetencia pComp1_q2 = new PuntajeCompetencia();
        pComp1_q2.setCompetencia(comp1);
        pComp1_q2.setPuntaje(9.0);

        PuntajeCompetencia pComp2_q2 = new PuntajeCompetencia();
        pComp2_q2.setCompetencia(comp2);
        pComp2_q2.setPuntaje(6.0); // Falla ponderación requerida de 8

        q2.setPuntajesCompetencias(List.of(pComp1_q2, pComp2_q2));

        // Mocking
        when(puestoRepository.findById(10L)).thenReturn(Optional.of(puesto));
        when(cuestionarioRepository.findByEvaluacionPuestoId(10L)).thenReturn(List.of(q1, q2));

        // Invocación
        EvaluacionDTO.ReporteOrdenMerito reporte = reporteService.generarOrdenMerito(10L, null);

        assertNotNull(reporte);
        assertEquals(1, reporte.candidatosAprobados().size());
        assertEquals("Lucas", reporte.candidatosAprobados().get(0).nombre());

        assertEquals(1, reporte.candidatosRechazadosOIncompletos().size());
        assertEquals("Marta", reporte.candidatosRechazadosOIncompletos().get(0).nombre());
    }
}
