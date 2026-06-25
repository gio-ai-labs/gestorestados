package com.gio.gestorestados.workitem;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.workitem.entity.WorkItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    List<WorkItem> findAllBySprintIdOrderByNombreAsc(Long sprintId);

    /**
     * Workitems de un sprint para el tablero (T13). Hace JOIN FETCH de sprint y usuario
     * (ambos LAZY) para evitar N+1 al mapear con open-in-view=false.
     */
    @Query("""
            select w from WorkItem w
            join fetch w.sprint
            left join fetch w.usuario
            where w.sprint.id = :sprintId
            order by w.nombre asc
            """)
    List<WorkItem> findAllParaTableroBySprintId(@Param("sprintId") Long sprintId);

    /**
     * Pagina los workitems de un sprint para el tablero (T27) con filtro opcional por
     * {@code estado} del workitem. La paginacion se resuelve en BD (no se hace JOIN FETCH
     * de la coleccion de tareas aqui para evitar la paginacion en memoria, HHH000104);
     * solo se traen asociaciones to-one (sprint, usuario) via JOIN FETCH, compatibles con
     * {@link Pageable}. Las tareas de la pagina se cargan en una segunda consulta. El orden
     * por defecto (nombre asc) lo aporta el {@link Pageable} desde el controller, consistente
     * con {@link #findAllParaTableroBySprintId(Long)}.
     */
    @Query("""
            select w from WorkItem w
            join fetch w.sprint
            left join fetch w.usuario
            where w.sprint.id = :sprintId
              and (:estado is null or w.estado = :estado)
            """)
    Page<WorkItem> buscarParaTableroBySprintId(@Param("sprintId") Long sprintId,
                                               @Param("estado") Estado estado,
                                               Pageable pageable);

    /** Sumatoria de horas de los workitems de un sprint (rollup nivel sprint, T8). */
    @Query("select coalesce(sum(w.horas), 0) from WorkItem w where w.sprint.id = :sprintId")
    BigDecimal sumHorasBySprintId(@Param("sprintId") Long sprintId);
}
