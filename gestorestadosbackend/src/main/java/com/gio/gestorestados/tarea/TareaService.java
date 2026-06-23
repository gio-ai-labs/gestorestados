package com.gio.gestorestados.tarea;

import com.gio.gestorestados.horas.HorasRollupService;
import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import com.gio.gestorestados.tarea.dto.HistorialResponse;
import com.gio.gestorestados.tarea.dto.TareaRequest;
import com.gio.gestorestados.tarea.dto.TareaResponse;
import com.gio.gestorestados.tarea.entity.Tarea;
import com.gio.gestorestados.tarea.entity.TareaEstadoHistorial;
import com.gio.gestorestados.tarea.mapper.TareaMapper;
import com.gio.gestorestados.usuario.UsuarioRepository;
import com.gio.gestorestados.usuario.entity.Usuario;
import com.gio.gestorestados.workitem.WorkItemRepository;
import com.gio.gestorestados.workitem.entity.WorkItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio de Tarea. Cada cambio de estado (creacion, edicion o tablero) queda
 * registrado en {@code tarea_estado_historial} (RF-6). El rollup de horas se conecta en T8.
 */
@Service
public class TareaService {

    private final TareaRepository repository;
    private final TareaEstadoHistorialRepository historialRepository;
    private final WorkItemRepository workItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorasRollupService horasRollupService;
    private final TareaMapper mapper;

    public TareaService(TareaRepository repository,
                       TareaEstadoHistorialRepository historialRepository,
                       WorkItemRepository workItemRepository,
                       UsuarioRepository usuarioRepository,
                       HorasRollupService horasRollupService,
                       TareaMapper mapper) {
        this.repository = repository;
        this.historialRepository = historialRepository;
        this.workItemRepository = workItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.horasRollupService = horasRollupService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listarPorWorkItem(Long workitemId) {
        verificarWorkItem(workitemId);
        return repository.findAllByWorkitemIdOrderByEstadoAscOrdenAscIdAsc(workitemId)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TareaResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<HistorialResponse> historial(Long tareaId) {
        verificarTarea(tareaId);
        return historialRepository.findAllByTareaIdOrderByFechaCambioAscIdAsc(tareaId)
                .stream().map(mapper::toHistorialResponse).toList();
    }

    @Transactional
    public TareaResponse crear(Long workitemId, TareaRequest req) {
        WorkItem workitem = workItemRepository.findById(workitemId)
                .orElseThrow(() -> new RecursoNoEncontradoException("WorkItem", workitemId));
        Usuario usuario = resolverUsuario(req.usuarioId());
        Tarea tarea = repository.save(mapper.toEntity(req, workitem, usuario));
        // Registro inicial del historial: null -> estado inicial.
        historialRepository.save(new TareaEstadoHistorial(tarea, null, tarea.getEstado()));
        // Rollup: las horas capturadas suben a workitem y sprint (T8).
        horasRollupService.recalcularDesdeWorkItem(workitem.getId());
        return mapper.toResponse(tarea);
    }

    @Transactional
    public TareaResponse actualizar(Long id, TareaRequest req) {
        Tarea tarea = buscar(id);
        Usuario usuario = resolverUsuario(req.usuarioId());
        mapper.updateEntity(tarea, req, usuario);
        // Si el request trae un estado distinto, se trata como cambio de estado (con historial).
        if (req.estado() != null) {
            registrarCambioEstado(tarea, req.estado());
        }
        // Rollup: las horas pudieron cambiar.
        horasRollupService.recalcularDesdeWorkItem(tarea.getWorkitem().getId());
        return mapper.toResponse(tarea);
    }

    @Transactional
    public TareaResponse cambiarEstado(Long id, Estado nuevoEstado) {
        Tarea tarea = buscar(id);
        registrarCambioEstado(tarea, nuevoEstado);
        return mapper.toResponse(tarea);
    }

    @Transactional
    public void eliminar(Long id) {
        Tarea tarea = buscar(id);
        Long workitemId = tarea.getWorkitem().getId();
        // El historial se elimina en cascada (FK ON DELETE CASCADE, T1).
        repository.delete(tarea);
        repository.flush();
        // Rollup tras el borrado: el workitem y su sprint pierden estas horas.
        horasRollupService.recalcularDesdeWorkItem(workitemId);
    }

    /** Cambia el estado y registra una entrada de historial solo si hubo transicion real. */
    private void registrarCambioEstado(Tarea tarea, Estado nuevoEstado) {
        Estado anterior = tarea.getEstado();
        if (anterior == nuevoEstado) {
            return;
        }
        tarea.setEstado(nuevoEstado);
        historialRepository.save(new TareaEstadoHistorial(tarea, anterior, nuevoEstado));
    }

    private Usuario resolverUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));
    }

    private void verificarWorkItem(Long workitemId) {
        if (!workItemRepository.existsById(workitemId)) {
            throw new RecursoNoEncontradoException("WorkItem", workitemId);
        }
    }

    private void verificarTarea(Long tareaId) {
        if (!repository.existsById(tareaId)) {
            throw new RecursoNoEncontradoException("Tarea", tareaId);
        }
    }

    private Tarea buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea", id));
    }
}
