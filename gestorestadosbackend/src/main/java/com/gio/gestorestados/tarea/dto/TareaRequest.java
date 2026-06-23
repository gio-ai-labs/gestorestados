package com.gio.gestorestados.tarea.dto;

import com.gio.gestorestados.shared.domain.Estado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO de entrada para crear/editar una tarea. A diferencia de Sprint/WorkItem,
 * la tarea SI captura {@code horas} (esfuerzo humano).
 *
 * @param nombre               obligatorio
 * @param descripcion          opcional
 * @param criteriosAceptacion  opcional
 * @param horas                esfuerzo humano (&gt;= 0); null -&gt; 0
 * @param usuarioId            usuario asignado (opcional)
 * @param estado               opcional; null -&gt; PENDIENTE al crear
 */
public record TareaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,

        String criteriosAceptacion,

        @PositiveOrZero(message = "Las horas no pueden ser negativas")
        BigDecimal horas,

        Long usuarioId,

        Estado estado
) {
}
