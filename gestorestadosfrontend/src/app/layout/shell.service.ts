import { Injectable, signal } from '@angular/core';

export interface Breadcrumb {
  label: string;
  /** routerLink opcional; el ultimo crumb suele ir sin link (pagina actual). */
  link?: string | unknown[];
}

/**
 * Estado del AppShell: titulo de la pagina y migas de pan. Cada feature lo actualiza
 * (drill-down: Proyectos > [Proyecto] > [Sprint] > [WorkItem] > Tablero).
 */
@Injectable({ providedIn: 'root' })
export class ShellService {
  readonly titulo = signal('Gestor de Estados');
  readonly breadcrumbs = signal<Breadcrumb[]>([]);

  set(titulo: string, breadcrumbs: Breadcrumb[] = []): void {
    this.titulo.set(titulo);
    this.breadcrumbs.set(breadcrumbs);
  }
}
