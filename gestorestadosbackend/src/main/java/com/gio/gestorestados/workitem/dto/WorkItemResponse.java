package com.gio.gestorestados.workitem.dto;

import com.gio.gestorestados.shared.domain.Estado;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de salida para un workitem. {@code horas} es derivada (solo lectura).
 * Incluye {@code usuarioNombre} para evitar una segunda llamada desde la UI.
 */
public record WorkItemResponse(
        Long id,
        Long sprintId,
        String nombre,
        String descripcion,
        String criteriosAceptacion,
        BigDecimal horas,
        Long usuarioId,
        String usuarioNombre,
        Estado estado,
        OffsetDateTime fechaCreacion,
        OffsetDateTime fechaActualizacion
) {
}
