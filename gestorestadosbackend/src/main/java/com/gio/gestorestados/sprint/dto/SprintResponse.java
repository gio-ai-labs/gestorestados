package com.gio.gestorestados.sprint.dto;

import com.gio.gestorestados.shared.domain.Estado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * DTO de salida para un sprint. {@code horas} es derivada (solo lectura).
 */
public record SprintResponse(
        Long id,
        Long proyectoId,
        String nombre,
        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Estado estado,
        BigDecimal horas,
        OffsetDateTime fechaCreacion,
        OffsetDateTime fechaActualizacion
) {
}
