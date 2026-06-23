package com.gio.gestorestados.tarea;

import com.gio.gestorestados.tarea.dto.CycleTimeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de cycle time por WorkItem (§6.7, paridad con {@code get_task_cycle_time} del MCP).
 */
@RestController
public class CycleTimeController {

    private final CycleTimeService service;

    public CycleTimeController(CycleTimeService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/workitems/{id}/cycle-time")
    public CycleTimeResponse cycleTime(@PathVariable Long id) {
        return service.calcular(id);
    }
}
