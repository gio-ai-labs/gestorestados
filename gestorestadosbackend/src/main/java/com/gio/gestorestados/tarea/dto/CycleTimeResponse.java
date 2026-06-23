package com.gio.gestorestados.tarea.dto;

import com.gio.gestorestados.shared.domain.Estado;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Respuesta de cycle time de un WorkItem, con paridad de shape respecto a
 * {@code get_task_cycle_time} del MCP (§7): {@code totalTasks}, {@code tasksWithCycleTime},
 * {@code avgCycleTimeMinutes} y {@code tasks[]}.
 */
public record CycleTimeResponse(
        Long workItemId,
        int totalTasks,
        int tasksWithCycleTime,
        Long avgCycleTimeMinutes,
        List<TaskCycleTime> tasks
) {

    /**
     * Cycle time de una tarea: minutos entre la PRIMERA entrada a EN_PROCESO
     * y la PRIMERA a COMPLETADO (RF-6). {@code cycleTimeMinutes} null si falta la transicion.
     */
    public record TaskCycleTime(
            Long taskId,
            String title,
            Estado state,
            OffsetDateTime inProgressAt,
            OffsetDateTime doneAt,
            Long cycleTimeMinutes,
            String note
    ) {
    }
}
