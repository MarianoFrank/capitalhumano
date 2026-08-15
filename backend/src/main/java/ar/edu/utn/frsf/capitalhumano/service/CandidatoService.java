package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.CandidatoDTO;
import ar.edu.utn.frsf.capitalhumano.mapper.CandidatoMapper;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import ar.edu.utn.frsf.capitalhumano.model.enums.Genero;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoDocumento;
import ar.edu.utn.frsf.capitalhumano.repository.CandidatoRepository;
import ar.edu.utn.frsf.capitalhumano.specification.CandidatoSpecification;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final CandidatoMapper candidatoMapper;

    public CandidatoService(CandidatoRepository candidatoRepository, CandidatoMapper candidatoMapper) {
        this.candidatoRepository = candidatoRepository;
        this.candidatoMapper = candidatoMapper;
    }

    public Page<CandidatoDTO.Resumen> obtenerCandidatosPaginados(
            String nombre, String apellido, Long numeroCandidato, Pageable pageable) {

        Specification<Candidato> spec = CandidatoSpecification.conFiltros(nombre, apellido, numeroCandidato);
        Page<Candidato> candidatosPage = candidatoRepository.findAll(spec, pageable);
        return candidatoMapper.aPaginaResumen(candidatosPage);
    }

    @Transactional
    public List<CandidatoDTO.Resumen> procesarCandidatosCsv(MultipartFile file) {
        List<CandidatoDTO.Resumen> candidatosProcesados = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (InputStream is = file.getInputStream()) {
            CsvMapper csvMapper = new CsvMapper();
            csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<CandidatoDTO.ImportarCsv> iterator = csvMapper.readerFor(CandidatoDTO.ImportarCsv.class)
                    .with(schema)
                    .readValues(is);

            while (iterator.hasNext()) {
                CandidatoDTO.ImportarCsv csvDto = iterator.next();

                if (csvDto.numeroDocumento() == null || csvDto.tipoDocumento() == null) {
                    continue;
                }

                Optional<Candidato> existingCandidateOpt = candidatoRepository.findByNumeroDocumento(csvDto.numeroDocumento());

                if (existingCandidateOpt.isPresent()) {
                    Candidato existing = existingCandidateOpt.get();
                    if (csvDto.nombre() != null) existing.setNombre(csvDto.nombre());
                    if (csvDto.apellido() != null) existing.setApellido(csvDto.apellido());
                    if (csvDto.email() != null) existing.setEmail(csvDto.email());
                    if (csvDto.escolaridad() != null) existing.setEscolaridad(csvDto.escolaridad());
                    if (csvDto.nacionalidad() != null) existing.setNacionalidad(csvDto.nacionalidad());

                    candidatoRepository.save(existing);
                    candidatosProcesados.add(candidatoMapper.aResumen(existing));
                } else {
                    Candidato newCandidate = new Candidato();
                    if (csvDto.numeroCandidato() != null && !csvDto.numeroCandidato().isBlank()) {
                        newCandidate.setNumeroCandidato(Long.parseLong(csvDto.numeroCandidato()));
                    }
                    newCandidate.setTipoDocumento(TipoDocumento.valueOf(csvDto.tipoDocumento().toUpperCase()));
                    newCandidate.setNumeroDocumento(csvDto.numeroDocumento());
                    newCandidate.setNombre(csvDto.nombre());
                    newCandidate.setApellido(csvDto.apellido());
                    if (csvDto.fechaNacimiento() != null && !csvDto.fechaNacimiento().isBlank()) {
                        newCandidate.setFechaNacimiento(LocalDate.parse(csvDto.fechaNacimiento(), formatter));
                    }
                    if (csvDto.genero() != null && !csvDto.genero().isBlank()) {
                        newCandidate.setGenero(Genero.valueOf(csvDto.genero().toUpperCase()));
                    }
                    newCandidate.setEmail(csvDto.email());
                    newCandidate.setEscolaridad(csvDto.escolaridad());
                    newCandidate.setNacionalidad(csvDto.nacionalidad());

                    Candidato saved = candidatoRepository.save(newCandidate);
                    candidatosProcesados.add(candidatoMapper.aResumen(saved));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage(), e);
        }

        return candidatosProcesados;
    }

    public Candidato buscarPorNumeroDocumento(String nroDocumento) {
        return candidatoRepository.findByNumeroDocumento(nroDocumento)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado para el documento: " + nroDocumento));
    }
}
