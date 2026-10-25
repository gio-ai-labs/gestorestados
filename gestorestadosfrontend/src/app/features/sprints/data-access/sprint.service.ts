import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, firstValueFrom, map } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Estado, Pagina, Sprint, SprintRequest, SprintTableroItem } from '../../../core/models';

/** Tamaño de página por defecto (coincide con el default del backend). */
const TAMANO_PAGINA = 20;

/** Acceso a datos de Sprints (anidados bajo proyecto). */
@Injectable({ providedIn: 'root' })
export class SprintService {
  private readonly http = inject(HttpClient);
  private readonly base = API_BASE_URL;

  /** Contenido de la página actual (lista visible). */
  readonly sprints = signal<Sprint[]>([]);
  readonly cargando = signal(false);

  /** Metadatos de la página actual. */
  readonly pagina = signal(0);
  readonly totalPaginas = signal(0);
  readonly totalElementos = signal(0);
  readonly tamano = signal(TAMANO_PAGINA);

  /**
   * Carga una página de sprints de un proyecto. `estado` filtra por estado de
   * sprint (opcional); `page` es base 0. Actualiza el contenido y los metadatos.
   */
  async cargar(proyectoId: number, page = 0, estado: Estado | null = null): Promise<void> {
    this.cargando.set(true);
    try {
      let params = new HttpParams().set('page', page).set('size', this.tamano());
      if (estado) {
        params = params.set('estado', estado);
      }
      const respuesta = await firstValueFrom(
        this.http.get<Pagina<Sprint>>(`${this.base}/proyectos/${proyectoId}/sprints`, { params }),
      );
      this.sprints.set(respuesta.content);
      this.pagina.set(respuesta.page);
      this.totalPaginas.set(respuesta.totalPages);
      this.totalElementos.set(respuesta.totalElements);
      this.tamano.set(respuesta.size);
    } finally {
      this.cargando.set(false);
    }
  }

  /**
   * Lista los sprints de un proyecto sin tocar el signal compartido `sprints`.
   * Útil para consumidores que necesitan la lista de forma puntual (p. ej. el
   * selector de sprint del tablero) sin acoplarse al estado de `sprints-list`.
   *
   * Devuelve solo el contenido de la primera página del endpoint paginado: el
   * selector del tablero conserva su firma `Observable<Sprint[]>`.
   */
  listarPorProyecto(proyectoId: number): Observable<Sprint[]> {
    const params = new HttpParams().set('page', 0).set('size', 1000);
    return this.http
      .get<Pagina<Sprint>>(`${this.base}/proyectos/${proyectoId}/sprints`, { params })
      .pipe(map((respuesta) => respuesta.content));
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
   * Tablero del sprint paginado a nivel de WORKITEM: cada item trae el workitem
   * con TODAS sus tareas (ordenadas por estado/orden/id desde el backend).
   * `estado` filtra por estado del WORKITEM (opcional); `page` es base 0.
   * Las tareas dentro de cada workitem no se paginan ni se filtran.
   * Sprint inexistente o `estado` inválido -> ProblemDetail (interceptor).
   */
  obtenerTablero(
    sprintId: number,
    page = 0,
    estado: Estado | null = null,
  ): Observable<Pagina<SprintTableroItem>> {
    let params = new HttpParams().set('page', page).set('size', TAMANO_PAGINA);
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<Pagina<SprintTableroItem>>(`${this.base}/sprints/${sprintId}/tablero`, {
      params,
    });
  }
}
