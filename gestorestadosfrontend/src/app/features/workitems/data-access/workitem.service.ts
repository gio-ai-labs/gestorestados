import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Estado, WorkItem, WorkItemRequest } from '../../../core/models';

/** Acceso a datos de WorkItems (anidados bajo sprint). */
@Injectable({ providedIn: 'root' })
export class WorkItemService {
  private readonly http = inject(HttpClient);
  private readonly base = API_BASE_URL;

  readonly workitems = signal<WorkItem[]>([]);
  readonly cargando = signal(false);

  async cargar(sprintId: number): Promise<void> {
    this.cargando.set(true);
    try {
      this.workitems.set(
        await firstValueFrom(this.http.get<WorkItem[]>(`${this.base}/sprints/${sprintId}/workitems`)),
      );
    } finally {
      this.cargando.set(false);
    }
  }

  obtener(id: number): Promise<WorkItem> {
    return firstValueFrom(this.http.get<WorkItem>(`${this.base}/workitems/${id}`));
  }

  crear(sprintId: number, req: WorkItemRequest): Promise<WorkItem> {
    return firstValueFrom(this.http.post<WorkItem>(`${this.base}/sprints/${sprintId}/workitems`, req));
  }

  actualizar(id: number, req: WorkItemRequest): Promise<WorkItem> {
    return firstValueFrom(this.http.put<WorkItem>(`${this.base}/workitems/${id}`, req));
  }

  cambiarEstado(id: number, estado: Estado): Promise<WorkItem> {
    return firstValueFrom(this.http.patch<WorkItem>(`${this.base}/workitems/${id}/estado`, { estado }));
  }

  eliminar(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.base}/workitems/${id}`));
  }
}
