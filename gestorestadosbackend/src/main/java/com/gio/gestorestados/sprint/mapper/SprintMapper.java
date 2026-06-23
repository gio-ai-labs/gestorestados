package com.gio.gestorestados.sprint.mapper;

import com.gio.gestorestados.proyecto.entity.Proyecto;
import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.sprint.dto.SprintRequest;
import com.gio.gestorestados.sprint.dto.SprintResponse;
import com.gio.gestorestados.sprint.entity.Sprint;
import org.springframework.stereotype.Component;

/**
 * Mapeo entre la entidad {@link Sprint} y sus DTOs. {@code horas} nunca se toma del request.
 */
@Component
public class SprintMapper {

    /** Crea un sprint bajo el proyecto dado. estado null -&gt; PENDIENTE. */
    public Sprint toEntity(SprintRequest req, Proyecto proyecto) {
        Sprint s = new Sprint();
        s.setProyecto(proyecto);
        s.setNombre(req.nombre());
        s.setDescripcion(req.descripcion());
        s.setFechaInicio(req.fechaInicio());
        s.setFechaFin(req.fechaFin());
        s.setEstado(req.estado() == null ? Estado.PENDIENTE : req.estado());
        return s;
    }

    /** Aplica cambios del request. No modifica proyecto ni horas (derivada). */
    public void updateEntity(Sprint s, SprintRequest req) {
        s.setNombre(req.nombre());
        s.setDescripcion(req.descripcion());
        s.setFechaInicio(req.fechaInicio());
        s.setFechaFin(req.fechaFin());
        if (req.estado() != null) {
            s.setEstado(req.estado());
        }
    }

    public SprintResponse toResponse(Sprint s) {
        return new SprintResponse(
                s.getId(),
                s.getProyecto().getId(),
                s.getNombre(),
                s.getDescripcion(),
                s.getFechaInicio(),
                s.getFechaFin(),
                s.getEstado(),
                s.getHoras(),
                s.getFechaCreacion(),
                s.getFechaActualizacion()
        );
    }
}
