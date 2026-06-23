package com.gio.gestorestados.sprint.dto;

import com.gio.gestorestados.tarea.dto.TareaResponse;
import com.gio.gestorestados.workitem.dto.WorkItemResponse;

import java.util.List;

/**
 * Vista compuesta del tablero: un workitem del sprint con su lista de tareas hijas (T13).
 * La agrupacion por estado se resuelve en la UI; el backend entrega las tareas ordenadas.
 */
public record SprintTableroResponse(WorkItemResponse workitem, List<TareaResponse> tareas) {
}
