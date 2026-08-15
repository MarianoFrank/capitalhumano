package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IaGeneracionService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public IaGeneracionService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public PreguntaDTO.IaRespuesta generarPregunta(PreguntaDTO.IaPeticion peticion) {
        String prompt = """
                Sos un experto psicotécnico y de Recursos Humanos especializado en el modelo de evaluación por competencias.
                Necesito que generes una pregunta situacional/comportamental desafiante basada en los siguientes datos:
                - Competencia: %s
                - Factor / Dimensión: %s
                - Nombre preliminar de la pregunta: %s
                - Descripción / Objetivo de evaluación: %s
                - Contexto adicional / Indicaciones especiales: %s

                Estructura de salida JSON esperada (ESTRICTA):
                {
                    "nombrePregunta": "Un título conciso y profesional para la pregunta",
                    "descripcion": "Breve justificación o contexto de evaluación",
                    "tipo": "SINGLE_CHOICE",
                    "texto": "El enunciado completo, claro y situacional de la pregunta",
                    "opciones": [
                        { "texto": "Opción excelente que refleja la conducta esperada", "ponderacion": 10 },
                        { "texto": "Opción adecuada pero no ideal", "ponderacion": 6 },
                        { "texto": "Opción neutral o poco efectiva", "ponderacion": 3 },
                        { "texto": "Opción totalmente desacertada o contraproducente", "ponderacion": 0 }
                    ]
                }
                
                IMPORTANTE: 
                - Devolvé ÚNICAMENTE el bloque JSON crudo sin bloques markdown ```json ``` ni texto adicional.
                - La suma de opciones debe ser exactamente 4 (para single choice) con ponderaciones decrecientes.
                """.formatted(
                peticion.nombreCompetencia(),
                peticion.nombreFactor(),
                peticion.nombrePregunta() != null ? peticion.nombrePregunta() : "",
                peticion.descripcion() != null ? peticion.descripcion() : "",
                peticion.contextoExtra() != null ? peticion.contextoExtra() : "Sin contexto adicional"
        );

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json",
                        "temperature", 0.7
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_API_URL + geminiApiKey,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("Respuesta vacía desde la API de Gemini.");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String jsonText = (String) parts.get(0).get("text");

            return objectMapper.readValue(jsonText, PreguntaDTO.IaRespuesta.class);

        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de IA de Gemini: " + e.getMessage(), e);
        }
    }
}
