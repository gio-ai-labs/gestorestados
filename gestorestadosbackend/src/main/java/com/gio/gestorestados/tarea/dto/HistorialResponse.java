package com.gio.gestorestados.tarea.dto;

import com.gio.gestorestados.shared.domain.Estado;

import java.time.OffsetDateTime;

/**
 * DTO de salida para una entrada del historial de estados de una tarea.
 */
public record HistorialResponse(
        Long id,
        Long tareaId,
        Estado estadoAnterior,
        Estado estadoNuevo,
        OffsetDateTime fechaCambio
) {
}
