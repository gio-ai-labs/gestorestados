package com.gio.gestorestados.proyecto.dto;

import java.time.OffsetDateTime;

/**
 * DTO de salida para un proyecto.
 */
public record ProyectoResponse(
        Long id,
        String nombre,
        String descripcion,
        OffsetDateTime fechaCreacion,
        OffsetDateTime fechaActualizacion
) {
}
