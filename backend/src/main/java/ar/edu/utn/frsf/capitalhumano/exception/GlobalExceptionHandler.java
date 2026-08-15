package ar.edu.utn.frsf.capitalhumano.exception;

import ar.edu.utn.frsf.capitalhumano.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de argumentos inválidos, parámetros incorrectos o validaciones
    // de precondición en la lógica de negocio (HTTP 400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Argumento inválido.";
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                errorMessage,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // Maneja violaciones de restricciones de integridad en base de datos (claves
    // únicas duplicadas, claves foráneas, etc.) (HTTP 409 Conflict)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "El registro ya existe o hay un conflicto con los datos ingresados.",
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // Maneja fallos de validación de Bean Validation (@Valid, @NotBlank, @NotNull,
    // etc.) en los cuerpos de petición (@RequestBody) (HTTP 400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> details = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            details.put(field, message);
        });

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Hay errores en los datos ingresados.",
                request.getRequestURI(),
                details);
        return ResponseEntity.status(status).body(body);
    }

    // Maneja fallos de validación en parámetros directos de métodos de controlador
    // (@RequestParam, @PathVariable, etc.) (HTTP 400 Bad Request)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidationException(
            HandlerMethodValidationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> details = new HashMap<>();

        ex.getParameterValidationResults().forEach(validationResult -> {
            String paramName = validationResult.getMethodParameter().getParameterName();
            String message = validationResult.getResolvableErrors().get(0).getDefaultMessage();
            details.put(paramName != null ? paramName : "param", message);
        });

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Hay errores de validación en la petición.",
                request.getRequestURI(),
                details);
        return ResponseEntity.status(status).body(body);
    }

    // Maneja solicitudes con JSON malformado, tipos de datos incompatibles o cuerpo
    // de solicitud no legible (HTTP 400 Bad Request)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "El cuerpo de la solicitud no es legible o tiene un formato incorrecto.",
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // Maneja cualquier otra excepción no controlada en tiempo de ejecución o error
    // general del servidor (HTTP 500 Internal Server Error)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado en el servidor.";
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                errorMessage,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
