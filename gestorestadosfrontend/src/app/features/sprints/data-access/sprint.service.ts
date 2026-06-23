import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, firstValueFrom } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Estado, Sprint, SprintRequest, SprintTableroItem } from '../../../core/models';

/** Acceso a datos de Sprints (anidados bajo proyecto). */
@Injectable({ providedIn: 'root' })
export class SprintService {
  private readonly http = inject(HttpClient);
  private readonly base = API_BASE_URL;

  readonly sprints = signal<Sprint[]>([]);
  readonly cargando = signal(false);

  async cargar(proyectoId: number): Promise<void> {
    this.cargando.set(true);
    try {
      this.sprints.set(await firstValueFrom(this.listarPorProyecto(proyectoId)));
    } finally {
      this.cargando.set(false);
    }
  }

  /**
   * Lista los sprints de un proyecto sin tocar el signal compartido `sprints`.
   * Útil para consumidores que necesitan la lista de forma puntual (p. ej. el
   * selector de sprint del tablero) sin acoplarse al estado de `sprints-list`.
   */
  listarPorProyecto(proyectoId: number): Observable<Sprint[]> {
    return this.http.get<Sprint[]>(`${this.base}/proyectos/${proyectoId}/sprints`);
  }

  obtener(id: number): Promise<Sprint> {
    return firstValueFrom(this.http.get<Sprint>(`${this.base}/sprints/${id}`));
  }

  crear(proyectoId: number, req: SprintRequest): Promise<Sprint> {
    return firstValueFrom(this.http.post<Sprint>(`${this.base}/proyectos/${proyectoId}/sprints`, req));
  }

  actualizar(id: number, req: SprintRequest): Promise<Sprint> {
    return firstValueFrom(this.http.put<Sprint>(`${this.base}/sprints/${id}`, req));
  }

  cambiarEstado(id: number, estado: Estado): Promise<Sprint> {
    return firstValueFrom(this.http.patch<Sprint>(`${this.base}/sprints/${id}/estado`, { estado }));
  }

  eliminar(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.base}/sprints/${id}`));
  }

  /**
   * Tablero del sprint: workitems con sus tareas (endpoint compuesto).
   * Las tareas vienen ordenadas por estado/orden/id desde el backend.
   * Sprint sin workitems -> []. Sprint inexistente -> ProblemDetail 404 (interceptor).
   */
  obtenerTablero(sprintId: number): Observable<SprintTableroItem[]> {
    return this.http.get<SprintTableroItem[]>(`${this.base}/sprints/${sprintId}/tablero`);
  }
}
