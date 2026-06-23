import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Estado, HistorialEntrada, Tarea, TareaRequest } from '../../../core/models';

/** Acceso a datos de Tareas (anidadas bajo workitem) e historial. */
@Injectable({ providedIn: 'root' })
export class TareaService {
  private readonly http = inject(HttpClient);
  private readonly base = API_BASE_URL;

  listar(workitemId: number): Promise<Tarea[]> {
    return firstValueFrom(this.http.get<Tarea[]>(`${this.base}/workitems/${workitemId}/tareas`));
  }

  crear(workitemId: number, req: TareaRequest): Promise<Tarea> {
    return firstValueFrom(this.http.post<Tarea>(`${this.base}/workitems/${workitemId}/tareas`, req));
  }

  actualizar(id: number, req: TareaRequest): Promise<Tarea> {
    return firstValueFrom(this.http.put<Tarea>(`${this.base}/tareas/${id}`, req));
  }

  cambiarEstado(id: number, estado: Estado): Promise<Tarea> {
    return firstValueFrom(this.http.patch<Tarea>(`${this.base}/tareas/${id}/estado`, { estado }));
  }

  eliminar(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.base}/tareas/${id}`));
  }

  historial(id: number): Promise<HistorialEntrada[]> {
    return firstValueFrom(this.http.get<HistorialEntrada[]>(`${this.base}/tareas/${id}/historial`));
  }
}
