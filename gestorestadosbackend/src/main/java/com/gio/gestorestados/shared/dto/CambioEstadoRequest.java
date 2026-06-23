package com.gio.gestorestados.shared.dto;

import com.gio.gestorestados.shared.domain.Estado;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para los endpoints {@code PATCH .../estado} (Sprint, WorkItem, Tarea).
 *
 * @param estado nuevo estado (obligatorio)
 */
public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        Estado estado
) {
}
