package com.gio.gestorestados.horas;

import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import com.gio.gestorestados.sprint.SprintRepository;
import com.gio.gestorestados.sprint.entity.Sprint;
import com.gio.gestorestados.tarea.TareaRepository;
import com.gio.gestorestados.workitem.WorkItemRepository;
import com.gio.gestorestados.workitem.entity.WorkItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Recalculo del rollup de horas de 3 niveles (RF-9, §6.6):
 * {@code WorkItem.horas = &Sigma; Tarea.horas} y {@code Sprint.horas = &Sigma; WorkItem.horas}.
 *
 * <p>Solo {@code Tarea.horas} se captura; los niveles superiores son derivados/solo lectura.
 * Se invoca al crear/editar(horas)/eliminar una tarea. Participa en la transaccion del
 * caller ({@code Propagation.REQUIRED}) para que el recalculo sea atomico con el cambio.
 */
@Service
public class HorasRollupService {

    private final TareaRepository tareaRepository;
    private final WorkItemRepository workItemRepository;
    private final SprintRepository sprintRepository;

    public HorasRollupService(TareaRepository tareaRepository,
                             WorkItemRepository workItemRepository,
                             SprintRepository sprintRepository) {
        this.tareaRepository = tareaRepository;
        this.workItemRepository = workItemRepository;
        this.sprintRepository = sprintRepository;
    }

    /**
     * Recalcula las horas del workitem (= &Sigma; tareas) y propaga al sprint (= &Sigma; workitems).
     *
     * @param workitemId id del workitem afectado por un cambio en sus tareas
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void recalcularDesdeWorkItem(Long workitemId) {
        WorkItem workitem = workItemRepository.findById(workitemId)
                .orElseThrow(() -> new RecursoNoEncontradoException("WorkItem", workitemId));

        BigDecimal totalWorkItem = tareaRepository.sumHorasByWorkitemId(workitemId);
        workitem.setHoras(totalWorkItem);

        recalcularSprint(workitem.getSprint().getId());
    }

    /** Recalcula las horas del sprint (= &Sigma; horas de sus workitems). */
    @Transactional(propagation = Propagation.REQUIRED)
    public void recalcularSprint(Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sprint", sprintId));
        BigDecimal totalSprint = workItemRepository.sumHorasBySprintId(sprintId);
        sprint.setHoras(totalSprint);
    }
}
