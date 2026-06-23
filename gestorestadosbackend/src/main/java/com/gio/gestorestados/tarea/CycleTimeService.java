package com.gio.gestorestados.tarea;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import com.gio.gestorestados.tarea.dto.CycleTimeResponse;
import com.gio.gestorestados.tarea.dto.CycleTimeResponse.TaskCycleTime;
import com.gio.gestorestados.tarea.entity.Tarea;
import com.gio.gestorestados.tarea.entity.TareaEstadoHistorial;
import com.gio.gestorestados.workitem.WorkItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculo de cycle time de un WorkItem a partir del historial de estados de sus tareas (RF-6).
 * Lectura agregada en memoria con transaccion corta de solo lectura (§6.4).
 */
@Service
public class CycleTimeService {

    private static final String NOTA_SIN_EN_PROCESO = "Sin transicion a EN_PROCESO registrada";
    private static final String NOTA_SIN_COMPLETADO = "Aun no ha llegado a COMPLETADO";

    private final WorkItemRepository workItemRepository;
    private final TareaRepository tareaRepository;
    private final TareaEstadoHistorialRepository historialRepository;

    public CycleTimeService(WorkItemRepository workItemRepository,
                           TareaRepository tareaRepository,
                           TareaEstadoHistorialRepository historialRepository) {
        this.workItemRepository = workItemRepository;
        this.tareaRepository = tareaRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional(readOnly = true)
    public CycleTimeResponse calcular(Long workItemId) {
        if (!workItemRepository.existsById(workItemId)) {
            throw new RecursoNoEncontradoException("WorkItem", workItemId);
        }

        List<Tarea> tareas = tareaRepository.findAllByWorkitemIdOrderByEstadoAscOrdenAscIdAsc(workItemId);
        List<TaskCycleTime> tasks = new ArrayList<>(tareas.size());

        long sumaMinutos = 0;
        int conCycleTime = 0;

        for (Tarea tarea : tareas) {
            TaskCycleTime tct = calcularTarea(tarea);
            tasks.add(tct);
            if (tct.cycleTimeMinutes() != null) {
                sumaMinutos += tct.cycleTimeMinutes();
                conCycleTime++;
            }
        }

        Long avg = conCycleTime > 0 ? Math.round((double) sumaMinutos / conCycleTime) : null;
        return new CycleTimeResponse(workItemId, tareas.size(), conCycleTime, avg, tasks);
    }

    private TaskCycleTime calcularTarea(Tarea tarea) {
        List<TareaEstadoHistorial> historial =
                historialRepository.findAllByTareaIdOrderByFechaCambioAscIdAsc(tarea.getId());

        OffsetDateTime inProgressAt = null;
        OffsetDateTime doneAt = null;

        // Primera entrada a EN_PROCESO y primera a COMPLETADO posterior a esta (RF-6).
        for (TareaEstadoHistorial h : historial) {
            if (h.getEstadoNuevo() == Estado.EN_PROCESO && inProgressAt == null) {
                inProgressAt = h.getFechaCambio();
            } else if (h.getEstadoNuevo() == Estado.COMPLETADO && inProgressAt != null && doneAt == null) {
                doneAt = h.getFechaCambio();
            }
        }

        if (inProgressAt != null && doneAt != null) {
            long minutos = Duration.between(inProgressAt, doneAt).toMinutes();
            return new TaskCycleTime(tarea.getId(), tarea.getNombre(), tarea.getEstado(),
                    inProgressAt, doneAt, minutos, null);
        }
        if (inProgressAt != null) {
            return new TaskCycleTime(tarea.getId(), tarea.getNombre(), tarea.getEstado(),
                    inProgressAt, null, null, NOTA_SIN_COMPLETADO);
        }
        return new TaskCycleTime(tarea.getId(), tarea.getNombre(), tarea.getEstado(),
                null, null, null, NOTA_SIN_EN_PROCESO);
    }
}
