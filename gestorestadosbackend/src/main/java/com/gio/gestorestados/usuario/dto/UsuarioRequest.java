package com.gio.gestorestados.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear/editar un usuario.
 *
 * @param nombre nombre del usuario (obligatorio)
 * @param email  email del usuario (obligatorio, formato valido, unico en el catalogo)
 * @param activo si el usuario esta activo; null se interpreta como {@code true} al crear
 */
public record UsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 320, message = "El email no puede exceder 320 caracteres")
        String email,

        Boolean activo
) {
}
