package com.gio.gestorestados.workitem;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import com.gio.gestorestados.sprint.SprintRepository;
import com.gio.gestorestados.sprint.entity.Sprint;
import com.gio.gestorestados.usuario.UsuarioRepository;
import com.gio.gestorestados.usuario.entity.Usuario;
import com.gio.gestorestados.workitem.dto.WorkItemRequest;
import com.gio.gestorestados.workitem.dto.WorkItemResponse;
import com.gio.gestorestados.workitem.entity.WorkItem;
import com.gio.gestorestados.workitem.mapper.WorkItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio de WorkItem. Cuelga del sprint; asignacion de usuario opcional.
 * Borrado en cascada a tareas (FK ON DELETE CASCADE, T1). {@code horas} es derivada (T8).
 */
@Service
public class WorkItemService {

    private final WorkItemRepository repository;
    private final SprintRepository sprintRepository;
    private final UsuarioRepository usuarioRepository;
    private final WorkItemMapper mapper;

    public WorkItemService(WorkItemRepository repository,
                          SprintRepository sprintRepository,
                          UsuarioRepository usuarioRepository,
                          WorkItemMapper mapper) {
        this.repository = repository;
        this.sprintRepository = sprintRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<WorkItemResponse> listarPorSprint(Long sprintId) {
        verificarSprint(sprintId);
        return repository.findAllBySprintIdOrderByNombreAsc(sprintId)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public WorkItemResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Transactional
    public WorkItemResponse crear(Long sprintId, WorkItemRequest req) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sprint", sprintId));
        Usuario usuario = resolverUsuario(req.usuarioId());
        WorkItem creado = repository.save(mapper.toEntity(req, sprint, usuario));
        return mapper.toResponse(creado);
    }

    @Transactional
    public WorkItemResponse actualizar(Long id, WorkItemRequest req) {
        WorkItem workitem = buscar(id);
        Usuario usuario = resolverUsuario(req.usuarioId());
        mapper.updateEntity(workitem, req, usuario);
        return mapper.toResponse(workitem);
    }

    @Transactional
    public WorkItemResponse cambiarEstado(Long id, Estado estado) {
        WorkItem workitem = buscar(id);
        workitem.setEstado(estado);
        return mapper.toResponse(workitem);
    }

    @Transactional
    public WorkItemResponse actualizarCriterios(Long id, String criteriosAceptacion) {
        WorkItem workitem = buscar(id);
        workitem.setCriteriosAceptacion(criteriosAceptacion);
        return mapper.toResponse(workitem);
    }

    @Transactional
    public void eliminar(Long id) {
        WorkItem workitem = buscar(id);
        repository.delete(workitem);
    }

    /** Resuelve el usuario asignado (opcional). 404 si se indica un id inexistente. */
    private Usuario resolverUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));
    }

    private void verificarSprint(Long sprintId) {
        if (!sprintRepository.existsById(sprintId)) {
            throw new RecursoNoEncontradoException("Sprint", sprintId);
        }
    }

    private WorkItem buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("WorkItem", id));
    }
}
