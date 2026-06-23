package com.gio.gestorestados.workitem.dto;

import com.gio.gestorestados.shared.domain.Estado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear/editar un workitem. {@code horas} NO se acepta: es derivada (rollup).
 *
 * @param nombre               obligatorio
 * @param descripcion          opcional
 * @param criteriosAceptacion  opcional
 * @param usuarioId            usuario asignado (opcional)
 * @param estado               opcional; null -&gt; PENDIENTE al crear
 */
public record WorkItemRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,

        String criteriosAceptacion,

        Long usuarioId,

        Estado estado
) {
}
