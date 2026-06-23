import { Estado } from './estado.model';

export interface Sprint {
  id: number;
  proyectoId: number;
  nombre: string;
  descripcion?: string;
  fechaInicio?: string;
  fechaFin?: string;
  estado: Estado;
  /** Derivada (rollup = Sigma horas de workitems). Solo lectura. */
  horas: number;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface SprintRequest {
  nombre: string;
  descripcion?: string;
  fechaInicio?: string;
  fechaFin?: string;
  estado?: Estado;
}
