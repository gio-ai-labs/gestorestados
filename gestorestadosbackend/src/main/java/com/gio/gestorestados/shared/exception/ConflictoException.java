package com.gio.gestorestados.shared.exception;

/**
 * Se lanza ante un conflicto de estado, p. ej. email de usuario duplicado.
 * Mapea a HTTP 409 Conflict (ver {@link GlobalExceptionHandler}).
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
