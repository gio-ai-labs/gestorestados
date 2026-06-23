import { Tarea } from './tarea.model';
import { WorkItem } from './workitem.model';

/**
 * Elemento del tablero de un sprint: un WorkItem con sus Tareas asociadas.
 * Coincide con la respuesta del endpoint compuesto
 * GET /sprints/{sprintId}/tablero (array de estos items).
 * Las tareas vienen ordenadas por estado/orden/id desde el backend.
 */
export interface SprintTableroItem {
  workitem: WorkItem;
  tareas: Tarea[];
}
