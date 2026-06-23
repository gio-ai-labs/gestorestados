package com.gio.gestorestados.usuario.dto;

import java.time.OffsetDateTime;

/**
 * DTO de salida para un usuario. Nunca se exponen las entidades directamente.
 */
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        boolean activo,
        OffsetDateTime fechaCreacion,
        OffsetDateTime fechaActualizacion
) {
}
