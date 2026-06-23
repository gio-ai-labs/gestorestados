/** Estado generico unificado (Sprint, WorkItem, Tarea). Coincide con el backend. */
export type Estado = 'PENDIENTE' | 'EN_PROCESO' | 'COMPLETADO';

export const ESTADOS: readonly Estado[] = ['PENDIENTE', 'EN_PROCESO', 'COMPLETADO'] as const;

/** Etiqueta legible para la UI. */
export const ESTADO_LABEL: Record<Estado, string> = {
  PENDIENTE: 'Pendiente',
  EN_PROCESO: 'En proceso',
  COMPLETADO: 'Completado',
};
