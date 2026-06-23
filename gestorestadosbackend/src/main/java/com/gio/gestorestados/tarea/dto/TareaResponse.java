package com.gio.gestorestados.tarea.dto;

import com.gio.gestorestados.shared.domain.Estado;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de salida para una tarea. Incluye {@code usuarioNombre} para el tablero/UI.
 */
public record TareaResponse(
        Long id,
        Long workitemId,
        String nombre,
        String descripcion,
        String criteriosAceptacion,
        BigDecimal horas,
        Long usuarioId,
        String usuarioNombre,
        Estado estado,
        Integer orden,
        OffsetDateTime fechaCreacion,
        OffsetDateTime fechaActualizacion
) {
}
