import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Usuario, UsuarioRequest } from '../../../core/models';

/** Acceso a datos del catalogo de Usuarios (compartido por T15 asignacion y T17 catalogo). */
@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/usuarios`;

  readonly usuarios = signal<Usuario[]>([]);
  readonly cargando = signal(false);

  async cargar(soloActivos = false): Promise<void> {
    this.cargando.set(true);
    try {
      this.usuarios.set(await this.listar(soloActivos));
    } finally {
      this.cargando.set(false);
    }
  }

  /** Lista usuarios; soloActivos=true excluye inactivos (para selects de asignacion, RF-7). */
  listar(soloActivos = false): Promise<Usuario[]> {
    const params = new HttpParams().set('soloActivos', String(soloActivos));
    return firstValueFrom(this.http.get<Usuario[]>(this.base, { params }));
  }

  crear(req: UsuarioRequest): Promise<Usuario> {
    return firstValueFrom(this.http.post<Usuario>(this.base, req));
  }

  actualizar(id: number, req: UsuarioRequest): Promise<Usuario> {
    return firstValueFrom(this.http.put<Usuario>(`${this.base}/${id}`, req));
  }

  eliminar(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.base}/${id}`));
  }
}
