package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.request.CandidatoCsvRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.CandidatoResumenResponse;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import ar.edu.utn.frsf.capitalhumano.model.enums.TipoDocumento;
import ar.edu.utn.frsf.capitalhumano.model.enums.Genero;
import ar.edu.utn.frsf.capitalhumano.repository.CandidatoRepository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;

    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public Page<CandidatoResumenResponse> obtenerCandidatosPaginados(String nombre, String apellido,
            Long numeroCandidato,
            Pageable pageable) {
        return candidatoRepository.findSummaryByFilters(nombre, apellido, numeroCandidato, pageable);
    }

    @Transactional
    public List<CandidatoResumenResponse> procesarCandidatosCsv(MultipartFile archivo) {
        List<CandidatoResumenResponse> candidatosProcesados = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            CsvMapper csvMapper = new CsvMapper();
            csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<CandidatoCsvRequest> iterator = csvMapper.readerFor(CandidatoCsvRequest.class)
                    .with(schema)
                    .readValues(archivo.getInputStream());

            List<CandidatoCsvRequest> csvCandidates = iterator.readAll();

            // 1. Extraemos todos los números de candidato del CSV de una pasada
            List<Long> candidateNumbers = csvCandidates.stream()
                    .map(dto -> Long.parseLong(dto.candidateNumber()))
                    .collect(Collectors.toList());

            // 2. Buscamos en la base de datos TODOS los que ya existen con una sola consulta
            List<Candidato> existingCandidates = candidatoRepository.findByCandidateNumberIn(candidateNumbers);

            // 3. Armamos un diccionario en memoria (Map) para buscar rapidísimo sin tocar la DB
            Map<Long, Candidato> existingMap = existingCandidates.stream()
                    .collect(Collectors.toMap(c -> c.getCandidateNumber(), c -> c));

            List<Candidato> newCandidatesToSave = new ArrayList<>();

            // 4. Recorremos el CSV separando los candidatos nuevos de los viejos
            for (CandidatoCsvRequest csvDto : csvCandidates) {
                Long candidateNumber = Long.parseLong(csvDto.candidateNumber());

                if (existingMap.containsKey(candidateNumber)) {
                    // Ya existe: lo agregamos directamente a la lista de respuesta
                    Candidato existing = existingMap.get(candidateNumber);
                    candidatosProcesados.add(new CandidatoResumenResponse(
                            existing.getId(), existing.getFirstName(), existing.getLastName(),
                            existing.getCandidateNumber()));
                } else {
                    // Es nuevo: armamos el objeto pero NO lo guardamos todavía
                    Candidato newCandidate = new Candidato();
                    newCandidate.setCandidateNumber(candidateNumber);
                    newCandidate.setDocumentType(TipoDocumento.valueOf(csvDto.documentType().toUpperCase()));
                    newCandidate.setDocumentNumber(csvDto.documentNumber());
                    newCandidate.setFirstName(csvDto.firstName());
                    newCandidate.setLastName(csvDto.lastName());
                    newCandidate.setBirthDate(LocalDate.parse(csvDto.birthDate(), formatter));
                    newCandidate.setGender(Genero.valueOf(csvDto.gender().toUpperCase()));
                    newCandidate.setEmail(csvDto.email());

                    if (csvDto.educationLevel() != null && !csvDto.educationLevel().isBlank()) {
                        newCandidate.setEducationLevel(csvDto.educationLevel());
                    }
                    if (csvDto.nationality() != null && !csvDto.nationality().isBlank()) {
                        newCandidate.setNationality(csvDto.nationality());
                    }

                    // Lo mandamos a la lista de espera
                    newCandidatesToSave.add(newCandidate);
                }
            }

            // 5. Guardamos todos los candidatos nuevos en un solo saque usando saveAll
            if (!newCandidatesToSave.isEmpty()) {
                List<Candidato> savedCandidates = candidatoRepository.saveAll(newCandidatesToSave);
                for (Candidato saved : savedCandidates) {
                    candidatosProcesados.add(new CandidatoResumenResponse(
                            saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getCandidateNumber()));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo CSV: " + e.getMessage());
        }

        // Devolvemos la lista completa (los que ya estaban + los recién creados)
        return candidatosProcesados;
    }

    public Candidato buscarPorNumeroDocumento(String numeroDocumento) {
        return candidatoRepository.findByDocumentNumber(numeroDocumento)
                .orElseThrow(
                        () -> new RuntimeException("Candidato con documento " + numeroDocumento + " no encontrado"));
    }
}
