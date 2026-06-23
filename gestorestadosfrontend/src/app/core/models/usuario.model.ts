export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface UsuarioRequest {
  nombre: string;
  email: string;
  activo?: boolean;
}
