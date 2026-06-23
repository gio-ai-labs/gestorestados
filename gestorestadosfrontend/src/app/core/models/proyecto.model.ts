export interface Proyecto {
  id: number;
  nombre: string;
  descripcion?: string;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface ProyectoRequest {
  nombre: string;
  descripcion?: string;
}
