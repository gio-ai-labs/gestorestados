package com.gio.gestorestados.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear/editar un proyecto.
 *
 * @param nombre      nombre del proyecto (obligatorio)
 * @param descripcion descripcion libre (opcional)
 */
public record ProyectoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion
) {
}
