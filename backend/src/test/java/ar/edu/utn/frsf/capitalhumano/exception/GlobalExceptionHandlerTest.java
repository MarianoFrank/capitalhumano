package ar.edu.utn.frsf.capitalhumano.exception;

import ar.edu.utn.frsf.capitalhumano.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test/path");
    }

    @Test
    @DisplayName("handleIllegalArgumentException - Devuelve 400 con mensaje y URI")
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Parámetro inválido");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Parámetro inválido", response.getBody().message());
        assertEquals("/api/test/path", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
        assertNull(response.getBody().validationErrors());
    }

    @Test
    @DisplayName("handleDataIntegrityViolation - Devuelve 409 Conflict")
    void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleDataIntegrityViolation(ex, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
        assertEquals("El registro ya existe o hay un conflicto con los datos ingresados.", response.getBody().message());
        assertEquals("/api/test/path", response.getBody().path());
    }

    @Test
    @DisplayName("handleValidationExceptions - Devuelve 400 con mapa validationErrors")
    void testHandleValidationExceptions() {
        FieldError fieldError = new FieldError("preguntaDTO", "nombre", "El nombre es obligatorio");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleValidationExceptions(methodArgumentNotValidException, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Hay errores en los datos ingresados.", response.getBody().message());
        assertEquals("/api/test/path", response.getBody().path());
        assertNotNull(response.getBody().validationErrors());
        assertEquals("El nombre es obligatorio", response.getBody().validationErrors().get("nombre"));
    }

    @Test
    @DisplayName("handleHttpMessageNotReadableException - Devuelve 400 Bad Request")
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(ex, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("El cuerpo de la solicitud no es legible o tiene un formato incorrecto.", response.getBody().message());
    }

    @Test
    @DisplayName("handleRuntimeException - Devuelve 500 Internal Server Error")
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Falla en la base de datos");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleRuntimeException(ex, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Falla en la base de datos", response.getBody().message());
        assertEquals("/api/test/path", response.getBody().path());
    }
}
