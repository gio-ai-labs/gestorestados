package com.gio.gestorestados.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones. Traduce cada excepcion a un {@link ProblemDetail}
 * (RFC 7807, nativo en Spring Boot 3) con respuesta homogenea para la UI y el MCP.
 *
 * <ul>
 *   <li>{@link RecursoNoEncontradoException} &rarr; 404</li>
 *   <li>{@link ReglaNegocioException} &rarr; 422</li>
 *   <li>{@link ConflictoException} &rarr; 409</li>
 *   <li>{@link MethodArgumentNotValidException} (Bean Validation) &rarr; 400 con detalle por campo</li>
 *   <li>Cualquier otra &rarr; 500</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final URI TIPO_BASE = URI.create("about:blank");

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail handleNoEncontrado(RecursoNoEncontradoException ex, WebRequest req) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail handleReglaNegocio(ReglaNegocioException ex, WebRequest req) {
        return construir(HttpStatus.UNPROCESSABLE_ENTITY, "Regla de negocio violada", ex.getMessage());
    }

    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail handleConflicto(ConflictoException ex, WebRequest req) {
        return construir(HttpStatus.CONFLICT, "Conflicto", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacion(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                errores.put(fe.getField(), fe.getDefaultMessage()));
        ProblemDetail pd = construir(HttpStatus.BAD_REQUEST, "Error de validacion",
                "Uno o mas campos son invalidos");
        pd.setProperty("errors", errores);
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNoLegible(HttpMessageNotReadableException ex, WebRequest req) {
        return construir(HttpStatus.BAD_REQUEST, "Cuerpo de la peticion invalido",
                "El cuerpo JSON esta mal formado o contiene valores no permitidos (p. ej. un estado invalido)");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex, WebRequest req) {
        log.error("Error no controlado procesando la peticion", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrio un error inesperado");
    }

    private ProblemDetail construir(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(TIPO_BASE);
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }
}
