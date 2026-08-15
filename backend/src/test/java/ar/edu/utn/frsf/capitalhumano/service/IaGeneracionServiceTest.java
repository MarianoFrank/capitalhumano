package ar.edu.utn.frsf.capitalhumano.service;

import ar.edu.utn.frsf.capitalhumano.dto.PreguntaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaGeneracionServiceTest {

    @Mock(answer = Answers.RETURNS_SELF)
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock(answer = Answers.RETURNS_SELF)
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Test
    @DisplayName("IaGeneracionService - generarPregunta invoca ChatClient y retorna entidad estructurada")
    void testGenerarPregunta() {
        when(chatClientBuilder.defaultSystem(anyString())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);

        PreguntaDTO.IaRespuesta mockRespuesta = new PreguntaDTO.IaRespuesta(
                "Pregunta de Liderazgo",
                "Evalúa resolución de conflictos",
                "SINGLE_CHOICE",
                "¿Cómo actuarías ante un desacuerdo en el equipo?",
                List.of(
                        new PreguntaDTO.OpcionGenerada("Facilito una reunión de diálogo abierta", 10),
                        new PreguntaDTO.OpcionGenerada("Escucho ambas partes por separado", 6),
                        new PreguntaDTO.OpcionGenerada("Dejo que lo resuelvan solos", 3),
                        new PreguntaDTO.OpcionGenerada("Ignoro la situación", 0)
                )
        );

        when(callResponseSpec.entity(PreguntaDTO.IaRespuesta.class)).thenReturn(mockRespuesta);

        IaGeneracionService service = new IaGeneracionService(chatClientBuilder);

        PreguntaDTO.IaPeticion peticion = new PreguntaDTO.IaPeticion(
                "Liderazgo",
                "Resolución de Conflictos",
                "Pregunta Situacional",
                "Evaluar manejo de desacuerdos",
                "Equipo multicultural"
        );

        PreguntaDTO.IaRespuesta respuesta = service.generarPregunta(peticion);

        assertNotNull(respuesta);
        assertEquals("Pregunta de Liderazgo", respuesta.nombrePregunta());
        assertEquals("SINGLE_CHOICE", respuesta.tipo());
        assertEquals(4, respuesta.opciones().size());
        assertEquals(10, respuesta.opciones().get(0).ponderacion());
        assertEquals(0, respuesta.opciones().get(3).ponderacion());
    }
}
