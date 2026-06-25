import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Pagina, Proyecto, ProyectoRequest } from '../../../core/models';

/** Tamaño de página por defecto (coincide con el default del backend). */
const TAMANO_PAGINA = 20;

/** Acceso a datos de Proyectos (signals + HttpClient). */
@Injectable({ providedIn: 'root' })
export class ProyectoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/proyectos`;

  /** Contenido de la página actual (lista visible). */
  readonly proyectos = signal<Proyecto[]>([]);
  readonly cargando = signal(false);

  /** Metadatos de la página actual. */
  readonly pagina = signal(0);
  readonly totalPaginas = signal(0);
  readonly totalElementos = signal(0);
  readonly tamano = signal(TAMANO_PAGINA);

  /**
   * Carga una página de proyectos. `nombre` filtra por coincidencia parcial
   * (ignore-case); `page` es base 0. Actualiza el contenido y los metadatos.
   */
  async cargar(page = 0, nombre = ''): Promise<void> {
    this.cargando.set(true);
    try {
      let params = new HttpParams().set('page', page).set('size', this.tamano());
      if (nombre.trim()) {
        params = params.set('nombre', nombre.trim());
      }
      const respuesta = await firstValueFrom(
        this.http.get<Pagina<Proyecto>>(this.base, { params }),
      );
      this.proyectos.set(respuesta.content);
      this.pagina.set(respuesta.page);
      this.totalPaginas.set(respuesta.totalPages);
      this.totalElementos.set(respuesta.totalElements);
      this.tamano.set(respuesta.size);
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
