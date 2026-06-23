package com.gio.gestorestados.workitem.mapper;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.sprint.entity.Sprint;
import com.gio.gestorestados.usuario.entity.Usuario;
import com.gio.gestorestados.workitem.dto.WorkItemRequest;
import com.gio.gestorestados.workitem.dto.WorkItemResponse;
import com.gio.gestorestados.workitem.entity.WorkItem;
import org.springframework.stereotype.Component;

/**
 * Mapeo entre la entidad {@link WorkItem} y sus DTOs. {@code horas} nunca se toma del request.
 */
@Component
public class WorkItemMapper {

    /** Crea un workitem bajo el sprint dado. usuario puede ser null. estado null -&gt; PENDIENTE. */
    public WorkItem toEntity(WorkItemRequest req, Sprint sprint, Usuario usuario) {
        WorkItem w = new WorkItem();
        w.setSprint(sprint);
        w.setNombre(req.nombre());
        w.setDescripcion(req.descripcion());
        w.setCriteriosAceptacion(req.criteriosAceptacion());
        w.setUsuario(usuario);
        w.setEstado(req.estado() == null ? Estado.PENDIENTE : req.estado());
        return w;
    }

    /** Aplica cambios del request. No modifica sprint ni horas (derivada). */
    public void updateEntity(WorkItem w, WorkItemRequest req, Usuario usuario) {
        w.setNombre(req.nombre());
        w.setDescripcion(req.descripcion());
        w.setCriteriosAceptacion(req.criteriosAceptacion());
        w.setUsuario(usuario);
        if (req.estado() != null) {
            w.setEstado(req.estado());
        }
    }

    public WorkItemResponse toResponse(WorkItem w) {
        Usuario u = w.getUsuario();
        return new WorkItemResponse(
                w.getId(),
                w.getSprint().getId(),
                w.getNombre(),
                w.getDescripcion(),
                w.getCriteriosAceptacion(),
                w.getHoras(),
                u == null ? null : u.getId(),
                u == null ? null : u.getNombre(),
                w.getEstado(),
                w.getFechaCreacion(),
                w.getFechaActualizacion()
        );
    }
}
