import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Proyecto, ProyectoRequest } from '../../../core/models';

/** Acceso a datos de Proyectos (signals + HttpClient). */
@Injectable({ providedIn: 'root' })
export class ProyectoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/proyectos`;

  readonly proyectos = signal<Proyecto[]>([]);
  readonly cargando = signal(false);

  async cargar(): Promise<void> {
    this.cargando.set(true);
    try {
      this.proyectos.set(await firstValueFrom(this.http.get<Proyecto[]>(this.base)));
    } finally {
      this.cargando.set(false);
    }
  }

  obtener(id: number): Promise<Proyecto> {
    return firstValueFrom(this.http.get<Proyecto>(`${this.base}/${id}`));
  }

  crear(req: ProyectoRequest): Promise<Proyecto> {
    return firstValueFrom(this.http.post<Proyecto>(this.base, req));
  }

  actualizar(id: number, req: ProyectoRequest): Promise<Proyecto> {
    return firstValueFrom(this.http.put<Proyecto>(`${this.base}/${id}`, req));
  }

  eliminar(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.base}/${id}`));
  }
}
