package com.gio.gestorestados.sprint;

import com.gio.gestorestados.sprint.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findAllByProyectoIdOrderByNombreAsc(Long proyectoId);

    /** Filtro por nombre dentro de un proyecto para {@code GET /proyectos/{id}/sprints?nombre=}. */
    List<Sprint> findAllByProyectoIdAndNombreContainingIgnoreCaseOrderByNombreAsc(
            Long proyectoId, String nombre);

    /** Resolucion por nombre exacto dentro de un proyecto (usado por el adaptador del MCP, §7). */
    Optional<Sprint> findFirstByProyectoIdAndNombreIgnoreCase(Long proyectoId, String nombre);
}
