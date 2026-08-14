package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.request.GenerarPreguntaRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.GenerarPreguntaResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IaGeneracionService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenerarPreguntaResponse generarPregunta(GenerarPreguntaRequest peticion) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                + apiKey;

        // Si están vacíos, le avisamos a la IA para que los genere. Si tienen texto, los usamos de contexto.
        String qName = (peticion.questionName() != null && !peticion.questionName().isBlank())
                ? peticion.questionName()
                : "[VACÍO] - Genera un nombre corto y descriptivo para esta pregunta.";
        String qDesc = (peticion.description() != null && !peticion.description().isBlank())
                ? peticion.description()
                : "[VACÍO] - Genera una breve descripción del objetivo de esta pregunta.";
        String extra = (peticion.extraContext() != null && !peticion.extraContext().isBlank())
                ? peticion.extraContext()
                : "Ninguno";

        // Prompt actualizado con los nuevos campos
        String prompt = String.format(
                "Sos un consultor experto en recursos humanos y evaluación de talento. " +
                        "Tu tarea es generar UNA pregunta para evaluar a un candidato en un sistema. " +
                        "Aquí tienes el contexto:\n" +
                        "- Competencia: '%s'\n" +
                        "- Factor específico: '%s'\n" +
                        "- Nombre de la métrica/pregunta: '%s'\n" +
                        "- Descripción/Objetivo: '%s'\n" +
                        "- Aclaraciones extra del consultor: '%s'\n\n" +
                        "REGLAS ESTRICTAS OBLIGATORIAS:\n" +
                        "1. Genera un mínimo de 3 opciones de respuesta (puedes generar 4 o 5 si es necesario).\n" +
                        "2. La suma TOTAL de los valores 'weight' (ponderación) de todas las opciones debe ser EXACTAMENTE 10.\n"
                        +
                        "3. Determina el tipo de pregunta ('SINGLE_CHOICE' si hay una sola opción correcta/ideal, o 'MULTIPLE_CHOICE' si hay varias opciones válidas que suman puntos).\n"
                        +
                        "4. Devuelve SOLO un JSON válido con este formato exacto, sin bloques markdown (```):\n" +
                        "{ \"questionName\": \"Nombre generado o el aportado\", \"description\": \"Descripción generada o la aportada\", \"type\": \"SINGLE_CHOICE\", \"text\": \"Texto de la pregunta\", \"options\": [ { \"text\": \"opción 1\", \"weight\": 10 }, { \"text\": \"opción 2\", \"weight\": 0 } ] }",
                peticion.competencyName(), peticion.factorName(), qName, qDesc, extra);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response);
            String aiTextResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text")
                    .asString();

            String cleanJson = aiTextResponse.replaceAll("(?i)```json", "").replaceAll("```", "").trim();

            return objectMapper.readValue(cleanJson, GenerarPreguntaResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Falló la generación con IA: " + e.getMessage());
        }
    }
}
