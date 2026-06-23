package com.gio.gestorestados.usuario.mapper;

import com.gio.gestorestados.usuario.dto.UsuarioRequest;
import com.gio.gestorestados.usuario.dto.UsuarioResponse;
import com.gio.gestorestados.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

/**
 * Mapeo entre la entidad {@link Usuario} y sus DTOs. Mapeo manual (sin dependencias extra).
 */
@Component
public class UsuarioMapper {

    /** Crea una nueva entidad a partir del request. {@code activo} null -&gt; true. */
    public Usuario toEntity(UsuarioRequest req) {
        Usuario u = new Usuario();
        u.setNombre(req.nombre());
        u.setEmail(req.email());
        u.setActivo(req.activo() == null || req.activo());
        return u;
    }

    /** Aplica los cambios del request sobre una entidad existente. */
    public void updateEntity(Usuario u, UsuarioRequest req) {
        u.setNombre(req.nombre());
        u.setEmail(req.email());
        if (req.activo() != null) {
            u.setActivo(req.activo());
        }
    }

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.isActivo(),
                u.getFechaCreacion(),
                u.getFechaActualizacion()
        );
    }
}
