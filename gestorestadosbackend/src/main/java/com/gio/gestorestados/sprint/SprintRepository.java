package com.gio.gestorestados.sprint;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.sprint.entity.Sprint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findAllByProyectoIdOrderByNombreAsc(Long proyectoId);

    /** Filtro por nombre dentro de un proyecto para {@code GET /proyectos/{id}/sprints?nombre=}. */
    List<Sprint> findAllByProyectoIdAndNombreContainingIgnoreCaseOrderByNombreAsc(
            Long proyectoId, String nombre);

    /** Resolucion por nombre exacto dentro de un proyecto (usado por el adaptador del MCP, §7). */
    Optional<Sprint> findFirstByProyectoIdAndNombreIgnoreCase(Long proyectoId, String nombre);

    /**
     * Listado paginado de sprints de un proyecto con filtros opcionales por nombre
     * (coincidencia, ignore case) y estado (T26). Los parametros nulos se ignoran,
     * de modo que un solo metodo cubre todas las combinaciones. El orden lo aporta
     * el {@link Pageable}.
     */
    @Query("""
            SELECT s FROM Sprint s
            WHERE s.proyecto.id = :proyectoId
              AND (CAST(:nombre AS string) IS NULL
                   OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%')))
              AND (:estado IS NULL OR s.estado = :estado)
            """)
    Page<Sprint> buscarPorProyecto(@Param("proyectoId") Long proyectoId,
                                   @Param("nombre") String nombre,
                                   @Param("estado") Estado estado,
                                   Pageable pageable);
}
