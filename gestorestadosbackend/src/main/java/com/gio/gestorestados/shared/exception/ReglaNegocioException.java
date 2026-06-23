package com.gio.gestorestados.shared.exception;

/**
 * Se lanza cuando se viola una regla de negocio (p. ej. fechaFin &lt; fechaInicio).
 * Mapea a HTTP 422 Unprocessable Entity (ver {@link GlobalExceptionHandler}).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
