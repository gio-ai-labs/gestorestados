package com.gio.gestorestados.workitem;

import com.gio.gestorestados.workitem.entity.WorkItem;
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

    /** Sumatoria de horas de los workitems de un sprint (rollup nivel sprint, T8). */
    @Query("select coalesce(sum(w.horas), 0) from WorkItem w where w.sprint.id = :sprintId")
    BigDecimal sumHorasBySprintId(@Param("sprintId") Long sprintId);
}
