package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IaGeneracionService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            Sos un psicotécnico y especialista en Recursos Humanos experto en el modelo de evaluación por competencias.
            Tu objetivo es diseñar preguntas situacionales y conductuales de alto impacto para procesos de selección de talento.
            Debes generar exactamente 4 opciones de respuesta con ponderaciones decrecientes:
            - 10 puntos: Opción óptima que refleja la conducta y desempeño esperado ideal.
            - 6 puntos: Opción adecuada o estándar con margen de mejora.
            - 3 puntos: Opción regular o poco efectiva.
            - 0 puntos: Opción desacertada, pasiva o contraproducente.
            """;

    private static final String USER_PROMPT_TEMPLATE = """
            Generá una pregunta situacional desafiante para evaluar la siguiente dimensión:
            - Competencia: {competencia}
            - Factor / Dimensión: {factor}
            - Título preliminar: {nombrePregunta}
            - Objetivo / Descripción: {descripcion}
            - Contexto adicional: {contextoExtra}
            """;

    public IaGeneracionService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public PreguntaDTO.IaRespuesta generarPregunta(PreguntaDTO.IaPeticion peticion) {
        return chatClient.prompt()
                .user(u -> u.text(USER_PROMPT_TEMPLATE)
                        .param("competencia", peticion.nombreCompetencia() != null ? peticion.nombreCompetencia() : "General")
                        .param("factor", peticion.nombreFactor() != null ? peticion.nombreFactor() : "General")
                        .param("nombrePregunta", peticion.nombrePregunta() != null ? peticion.nombrePregunta() : "Sin título previo")
                        .param("descripcion", peticion.descripcion() != null ? peticion.descripcion() : "Evaluación general del factor")
                        .param("contextoExtra", peticion.contextoExtra() != null ? peticion.contextoExtra() : "Sin contexto adicional"))
                .call()
                .entity(PreguntaDTO.IaRespuesta.class);
    }
}
