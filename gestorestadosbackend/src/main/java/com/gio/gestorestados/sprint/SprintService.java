package com.gio.gestorestados.sprint;

import com.gio.gestorestados.proyecto.ProyectoRepository;
import com.gio.gestorestados.proyecto.entity.Proyecto;
import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import com.gio.gestorestados.shared.exception.ReglaNegocioException;
import com.gio.gestorestados.sprint.dto.SprintRequest;
import com.gio.gestorestados.sprint.dto.SprintResponse;
import com.gio.gestorestados.sprint.dto.SprintTableroResponse;
import com.gio.gestorestados.sprint.entity.Sprint;
import com.gio.gestorestados.sprint.mapper.SprintMapper;
import com.gio.gestorestados.tarea.TareaRepository;
import com.gio.gestorestados.tarea.entity.Tarea;
import com.gio.gestorestados.tarea.mapper.TareaMapper;
import com.gio.gestorestados.workitem.WorkItemRepository;
import com.gio.gestorestados.workitem.entity.WorkItem;
import com.gio.gestorestados.workitem.mapper.WorkItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reglas de negocio de Sprint. Valida fechas (fechaFin &gt;= fechaInicio) y cuelga del proyecto.
 * El borrado elimina en cascada workitems y tareas (FKs ON DELETE CASCADE, T1).
 */
@Service
public class SprintService {

    private final SprintRepository repository;
    private final ProyectoRepository proyectoRepository;
    private final SprintMapper mapper;
    private final WorkItemRepository workItemRepository;
    private final TareaRepository tareaRepository;
    private final WorkItemMapper workItemMapper;
    private final TareaMapper tareaMapper;

    public SprintService(SprintRepository repository,
                         ProyectoRepository proyectoRepository,
                         SprintMapper mapper,
                         WorkItemRepository workItemRepository,
                         TareaRepository tareaRepository,
                         WorkItemMapper workItemMapper,
                         TareaMapper tareaMapper) {
        this.repository = repository;
        this.proyectoRepository = proyectoRepository;
        this.mapper = mapper;
        this.workItemRepository = workItemRepository;
        this.tareaRepository = tareaRepository;
        this.workItemMapper = workItemMapper;
        this.tareaMapper = tareaMapper;
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> listarPorProyecto(Long proyectoId, String nombre) {
        verificarProyecto(proyectoId);
        List<Sprint> sprints = (nombre == null || nombre.isBlank())
                ? repository.findAllByProyectoIdOrderByNombreAsc(proyectoId)
                : repository.findAllByProyectoIdAndNombreContainingIgnoreCaseOrderByNombreAsc(
                        proyectoId, nombre.trim());
        return sprints.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SprintResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    /**
     * Tablero del sprint (T13): todos sus workitems, cada uno con su lista de tareas.
     * Resuelve el conjunto en dos consultas con JOIN FETCH (sin N+1). Sprint inexistente
     * lanza {@link RecursoNoEncontradoException} (ProblemDetail 404); sin workitems -&gt; lista vacia.
     */
    @Transactional(readOnly = true)
    public List<SprintTableroResponse> obtenerTablero(Long sprintId) {
        buscar(sprintId);
        List<WorkItem> workitems = workItemRepository.findAllParaTableroBySprintId(sprintId);
        if (workitems.isEmpty()) {
            return List.of();
        }
        List<Long> ids = workitems.stream().map(WorkItem::getId).toList();
        Map<Long, List<Tarea>> tareasPorWorkitem = tareaRepository
                .findAllParaTableroByWorkitemIds(ids).stream()
                .collect(Collectors.groupingBy(t -> t.getWorkitem().getId()));
        return workitems.stream()
                .map(w -> new SprintTableroResponse(
                        workItemMapper.toResponse(w),
                        tareasPorWorkitem.getOrDefault(w.getId(), List.of()).stream()
                                .map(tareaMapper::toResponse).toList()))
                .toList();
    }

    @Transactional
    public SprintResponse crear(Long proyectoId, SprintRequest req) {
        validarFechas(req);
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto", proyectoId));
        Sprint creado = repository.save(mapper.toEntity(req, proyecto));
        return mapper.toResponse(creado);
    }

    @Transactional
    public SprintResponse actualizar(Long id, SprintRequest req) {
        validarFechas(req);
        Sprint sprint = buscar(id);
        mapper.updateEntity(sprint, req);
        return mapper.toResponse(sprint);
    }

    @Transactional
    public SprintResponse cambiarEstado(Long id, Estado estado) {
        Sprint sprint = buscar(id);
        sprint.setEstado(estado);
        return mapper.toResponse(sprint);
    }

    @Transactional
    public void eliminar(Long id) {
        Sprint sprint = buscar(id);
        repository.delete(sprint);
    }

    private void validarFechas(SprintRequest req) {
        if (req.fechaInicio() != null && req.fechaFin() != null
                && req.fechaFin().isBefore(req.fechaInicio())) {
            throw new ReglaNegocioException("La fecha fin no puede ser anterior a la fecha inicio");
        }
    }

    private void verificarProyecto(Long proyectoId) {
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new RecursoNoEncontradoException("Proyecto", proyectoId);
        }
    }

    private Sprint buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sprint", id));
    }
}
