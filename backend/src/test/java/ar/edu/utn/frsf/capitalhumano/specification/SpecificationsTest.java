package ar.edu.utn.frsf.capitalhumano.specification;

import ar.edu.utn.frsf.capitalhumano.model.*;
import ar.edu.utn.frsf.capitalhumano.model.enums.Genero;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoCompetencia;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoDocumento;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoPregunta;
import ar.edu.utn.frsf.capitalhumano.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SpecificationsTest {

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private FactorRepository factorRepository;

    @Autowired
    private CompetenciaRepository competenciaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PuestoRepository puestoRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private ConsultorRepository consultorRepository;

    @BeforeEach
    void setUpData() {
        candidatoRepository.deleteAll();
        preguntaRepository.deleteAll();
        factorRepository.deleteAll();
        competenciaRepository.deleteAll();
        evaluacionRepository.deleteAll();
        puestoRepository.deleteAll();
        empresaRepository.deleteAll();
        consultorRepository.deleteAll();

        // Candidatos
        Candidato c1 = new Candidato();
        c1.setNumeroCandidato(101L);
        c1.setNombre("Mariano");
        c1.setApellido("Gonzalez");
        c1.setNumeroDocumento("30123456");
        c1.setTipoDocumento(TipoDocumento.DNI);
        c1.setGenero(Genero.H);
        c1.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        c1.setEmail("mariano@test.com");
        candidatoRepository.save(c1);

        Candidato c2 = new Candidato();
        c2.setNumeroCandidato(102L);
        c2.setNombre("Lucia");
        c2.setApellido("Fernandez");
        c2.setNumeroDocumento("30654321");
        c2.setTipoDocumento(TipoDocumento.DNI);
        c2.setGenero(Genero.M);
        c2.setFechaNacimiento(LocalDate.of(1992, 2, 2));
        c2.setEmail("lucia@test.com");
        candidatoRepository.save(c2);

        // Competencia & Factor & Preguntas
        Competencia comp = new Competencia();
        comp.setCodigo("COMP-01");
        comp.setNombre("Adaptabilidad");
        comp.setTipo(TipoCompetencia.CONDUCTUAL);
        competenciaRepository.save(comp);

        Factor fact = new Factor();
        fact.setCodigo("FACT-01");
        fact.setNombre("Flexibilidad");
        fact.setNumeroOrden(1);
        fact.setCompetencia(comp);
        factorRepository.save(fact);

        Pregunta q1 = new Pregunta();
        q1.setNombre("¿Cómo maneja los cambios de planes?");
        q1.setTexto("Texto pregunta");
        q1.setVersion(1);
        q1.setTipo(TipoPregunta.SINGLE_CHOICE);
        q1.setFactor(fact);
        preguntaRepository.save(q1);

        Pregunta q2 = new Pregunta();
        q2.setNombre("Resolución rápida ante imprevistos");
        q2.setTexto("Texto pregunta 2");
        q2.setVersion(1);
        q2.setTipo(TipoPregunta.SINGLE_CHOICE);
        q2.setFactor(fact);
        q2.setFechaBaja(LocalDateTime.now()); // Borrada
        preguntaRepository.save(q2);

        // Empresa, Puesto & Evaluacion
        Empresa emp = new Empresa();
        emp.setNombre("Acme Inc");
        empresaRepository.save(emp);

        Puesto p1 = new Puesto();
        p1.setCodigo("PST-01");
        p1.setNombre("Desarrollador Fullstack");
        p1.setEmpresa(emp);
        puestoRepository.save(p1);

        Consultor cons = new Consultor();
        cons.setUsername("consultor1");
        consultorRepository.save(cons);

        Evaluacion eval = new Evaluacion();
        eval.setCodigo("EVAL-101");
        eval.setPuesto(p1);
        eval.setConsultor(cons);
        eval.setFechaCierre(LocalDateTime.now().plusDays(5));
        eval.setDuracion(60);
        evaluacionRepository.save(eval);
    }

    @Test
    @DisplayName("CandidatoSpecification - Filtros dinámicos combinados")
    void testCandidatoSpecification() {
        Specification<Candidato> spec1 = CandidatoSpecification.conFiltros("marian", null, null);
        List<Candidato> res1 = candidatoRepository.findAll(spec1);
        assertEquals(1, res1.size());
        assertEquals("Mariano", res1.get(0).getNombre());

        Specification<Candidato> spec2 = CandidatoSpecification.conFiltros(null, "Fernandez", null);
        List<Candidato> res2 = candidatoRepository.findAll(spec2);
        assertEquals(1, res2.size());
        assertEquals("Lucia", res2.get(0).getNombre());

        Specification<Candidato> spec3 = CandidatoSpecification.conFiltros(null, null, 101L);
        List<Candidato> res3 = candidatoRepository.findAll(spec3);
        assertEquals(1, res3.size());
        assertEquals(101L, res3.get(0).getNumeroCandidato());

        Specification<Candidato> spec4 = CandidatoSpecification.conFiltros(null, null, null);
        List<Candidato> res4 = candidatoRepository.findAll(spec4);
        assertEquals(2, res4.size());
    }

    @Test
    @DisplayName("PreguntaSpecification - Filtrado por competencia, factor, nombre y no eliminadas")
    void testPreguntaSpecification() {
        Competencia comp = competenciaRepository.findAll().get(0);
        Factor fact = factorRepository.findAll().get(0);

        Specification<Pregunta> spec = PreguntaSpecification.conFiltros(comp.getId(), fact.getId(), "cambios");
        Page<Pregunta> page = preguntaRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("¿Cómo maneja los cambios de planes?", page.getContent().get(0).getNombre());
        assertNull(page.getContent().get(0).getFechaBaja());
    }

    @Test
    @DisplayName("PuestoSpecification - Filtrado por empresa, código, nombre y verificación de evaluaciones")
    void testPuestoSpecification() {
        Empresa emp = empresaRepository.findAll().get(0);

        Specification<Puesto> spec = PuestoSpecification.conFiltrosReporte(emp.getId(), "Fullstack", "PST-01");
        Page<Puesto> page = puestoRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Desarrollador Fullstack", page.getContent().get(0).getNombre());
    }

    @Test
    @DisplayName("FactorSpecification - Filtrado por competencia y no eliminados")
    void testFactorSpecification() {
        Competencia comp = competenciaRepository.findAll().get(0);

        Specification<Factor> spec = FactorSpecification.conFiltros(comp.getId());
        List<Factor> list = factorRepository.findAll(spec);

        assertEquals(1, list.size());
        assertEquals("Flexibilidad", list.get(0).getNombre());
    }
}
