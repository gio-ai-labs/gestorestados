package com.gio.gestorestados.proyecto.mapper;

import com.gio.gestorestados.proyecto.dto.ProyectoRequest;
import com.gio.gestorestados.proyecto.dto.ProyectoResponse;
import com.gio.gestorestados.proyecto.entity.Proyecto;
import org.springframework.stereotype.Component;

/**
 * Mapeo entre la entidad {@link Proyecto} y sus DTOs.
 */
@Component
public class ProyectoMapper {

    public Proyecto toEntity(ProyectoRequest req) {
        Proyecto p = new Proyecto();
        p.setNombre(req.nombre());
        p.setDescripcion(req.descripcion());
        return p;
    }

    public void updateEntity(Proyecto p, ProyectoRequest req) {
        p.setNombre(req.nombre());
        p.setDescripcion(req.descripcion());
    }

    public ProyectoResponse toResponse(Proyecto p) {
        return new ProyectoResponse(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getFechaCreacion(),
                p.getFechaActualizacion()
        );
    }
}
