package com.gio.gestorestados.shared.exception;

/**
 * Se lanza cuando un recurso solicitado no existe. Mapea a HTTP 404 (ver {@link GlobalExceptionHandler}).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("%s con id %s no encontrado".formatted(recurso, id));
    }
}
