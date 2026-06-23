package com.gio.gestorestados.proyecto;

import com.gio.gestorestados.proyecto.dto.ProyectoRequest;
import com.gio.gestorestados.proyecto.dto.ProyectoResponse;
import com.gio.gestorestados.proyecto.entity.Proyecto;
import com.gio.gestorestados.proyecto.mapper.ProyectoMapper;
import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio de Proyecto. El borrado elimina en cascada sprints, workitems
 * y tareas via las FKs {@code ON DELETE CASCADE} de la BD (T1).
 */
@Service
public class ProyectoService {

    private final ProyectoRepository repository;
    private final ProyectoMapper mapper;

    public ProyectoService(ProyectoRepository repository, ProyectoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listar(String nombre) {
        List<Proyecto> proyectos = (nombre == null || nombre.isBlank())
                ? repository.findAllByOrderByNombreAsc()
                : repository.findAllByNombreContainingIgnoreCaseOrderByNombreAsc(nombre.trim());
        return proyectos.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProyectoResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Transactional
    public ProyectoResponse crear(ProyectoRequest req) {
        Proyecto creado = repository.save(mapper.toEntity(req));
        return mapper.toResponse(creado);
    }

    @Transactional
    public ProyectoResponse actualizar(Long id, ProyectoRequest req) {
        Proyecto proyecto = buscar(id);
        mapper.updateEntity(proyecto, req);
        return mapper.toResponse(proyecto);
    }

    /** Borrado en cascada hacia abajo del arbol (cascada en BD). */
    @Transactional
    public void eliminar(Long id) {
        Proyecto proyecto = buscar(id);
        repository.delete(proyecto);
    }

    private Proyecto buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto", id));
    }
}
