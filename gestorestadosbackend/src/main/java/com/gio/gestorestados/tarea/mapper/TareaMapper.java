package com.gio.gestorestados.tarea.mapper;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.tarea.dto.HistorialResponse;
import com.gio.gestorestados.tarea.dto.TareaRequest;
import com.gio.gestorestados.tarea.dto.TareaResponse;
import com.gio.gestorestados.tarea.entity.Tarea;
import com.gio.gestorestados.tarea.entity.TareaEstadoHistorial;
import com.gio.gestorestados.usuario.entity.Usuario;
import com.gio.gestorestados.workitem.entity.WorkItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapeo entre {@link Tarea} / {@link TareaEstadoHistorial} y sus DTOs.
 */
@Component
public class TareaMapper {

    /** Crea una tarea bajo el workitem dado. horas null -&gt; 0; estado null -&gt; PENDIENTE. */
    public Tarea toEntity(TareaRequest req, WorkItem workitem, Usuario usuario) {
        Tarea t = new Tarea();
        t.setWorkitem(workitem);
        t.setNombre(req.nombre());
        t.setDescripcion(req.descripcion());
        t.setCriteriosAceptacion(req.criteriosAceptacion());
        t.setHoras(req.horas() == null ? BigDecimal.ZERO : req.horas());
        t.setUsuario(usuario);
        t.setEstado(req.estado() == null ? Estado.PENDIENTE : req.estado());
        return t;
    }

    /**
     * Aplica cambios del request sobre la tarea. No toca el estado (lo gestiona el service
     * para registrar historial). No toca el workitem.
     */
    public void updateEntity(Tarea t, TareaRequest req, Usuario usuario) {
        t.setNombre(req.nombre());
        t.setDescripcion(req.descripcion());
        t.setCriteriosAceptacion(req.criteriosAceptacion());
        t.setHoras(req.horas() == null ? BigDecimal.ZERO : req.horas());
        t.setUsuario(usuario);
    }

    public TareaResponse toResponse(Tarea t) {
        Usuario u = t.getUsuario();
        return new TareaResponse(
                t.getId(),
                t.getWorkitem().getId(),
                t.getNombre(),
                t.getDescripcion(),
                t.getCriteriosAceptacion(),
                t.getHoras(),
                u == null ? null : u.getId(),
                u == null ? null : u.getNombre(),
                t.getEstado(),
                t.getOrden(),
                t.getFechaCreacion(),
                t.getFechaActualizacion()
        );
    }

    public HistorialResponse toHistorialResponse(TareaEstadoHistorial h) {
        return new HistorialResponse(
                h.getId(),
                h.getTarea().getId(),
                h.getEstadoAnterior(),
                h.getEstadoNuevo(),
                h.getFechaCambio()
        );
    }
}
