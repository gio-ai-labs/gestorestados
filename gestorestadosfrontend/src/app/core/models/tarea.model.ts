import { Estado } from './estado.model';

export interface Tarea {
  id: number;
  workitemId: number;
  nombre: string;
  descripcion?: string;
  criteriosAceptacion?: string;
  /** Capturada (esfuerzo humano). */
  horas: number;
  usuarioId?: number;
  usuarioNombre?: string;
  estado: Estado;
  orden: number;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface TareaRequest {
  nombre: string;
  descripcion?: string;
  criteriosAceptacion?: string;
  horas?: number;
  usuarioId?: number | null;
  estado?: Estado;
}

export interface HistorialEntrada {
  id: number;
  tareaId: number;
  estadoAnterior?: Estado;
  estadoNuevo: Estado;
  fechaCambio: string;
}
