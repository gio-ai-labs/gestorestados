package com.gio.gestorestados.sprint.dto;

import com.gio.gestorestados.shared.domain.Estado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para crear/editar un sprint. {@code horas} NO se acepta: es derivada (rollup).
 *
 * @param nombre       obligatorio
 * @param descripcion  opcional
 * @param fechaInicio  opcional; si ambas fechas estan presentes, fechaFin &gt;= fechaInicio
 * @param fechaFin     opcional
 * @param estado       opcional; null -&gt; PENDIENTE al crear
 */
public record SprintRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,

        LocalDate fechaInicio,

        LocalDate fechaFin,

        Estado estado
) {
}
