package com.gio.gestorestados.tarea;

import com.gio.gestorestados.tarea.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    /** Tareas de un workitem ordenadas para el tablero (por estado y orden). */
    List<Tarea> findAllByWorkitemIdOrderByEstadoAscOrdenAscIdAsc(Long workitemId);

    /**
     * Tareas de varios workitems en una sola consulta para el tablero del sprint (T13).
     * Hace JOIN FETCH de workitem y usuario (LAZY) para evitar N+1 con open-in-view=false.
     */
    @Query("""
            select t from Tarea t
            join fetch t.workitem
            left join fetch t.usuario
            where t.workitem.id in :workitemIds
            order by t.workitem.id asc, t.estado asc, t.orden asc, t.id asc
            """)
    List<Tarea> findAllParaTableroByWorkitemIds(@Param("workitemIds") Collection<Long> workitemIds);

    /** Sumatoria de horas de las tareas de un workitem (rollup nivel workitem, T8). */
    @Query("select coalesce(sum(t.horas), 0) from Tarea t where t.workitem.id = :workitemId")
    BigDecimal sumHorasByWorkitemId(@Param("workitemId") Long workitemId);
}
