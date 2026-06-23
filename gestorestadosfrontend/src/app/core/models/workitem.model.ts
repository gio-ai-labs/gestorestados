import { Estado } from './estado.model';

export interface WorkItem {
  id: number;
  sprintId: number;
  nombre: string;
  descripcion?: string;
  criteriosAceptacion?: string;
  /** Derivada (rollup = Sigma horas de tareas). Solo lectura. */
  horas: number;
  usuarioId?: number;
  usuarioNombre?: string;
  estado: Estado;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface WorkItemRequest {
  nombre: string;
  descripcion?: string;
  criteriosAceptacion?: string;
  usuarioId?: number | null;
  estado?: Estado;
}
