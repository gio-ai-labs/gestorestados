import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ShellService } from '../../layout/shell.service';

/**
 * Landing inicial (placeholder de T11). Las features reales (Proyectos, Usuarios)
 * se incorporan en T13/T17.
 */
@Component({
  selector: 'app-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  template: `
    <div class="grid min-h-[60vh] place-items-center px-6 py-16">
      <div class="max-w-md text-center">
        <div class="mx-auto mb-6 grid size-14 place-items-center rounded-2xl bg-gradient-to-br from-brand-500 to-brand-700 text-white shadow-md shadow-brand-600/30">
          <svg viewBox="0 0 24 24" fill="none" class="size-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 7l9-4 9 4-9 4-9-4Z" /><path d="M3 12l9 4 9-4" /><path d="M3 17l9 4 9-4" />
          </svg>
        </div>
        <h2 class="font-display text-2xl font-bold text-ink-900 dark:text-slate-100">Bienvenido al Gestor de Estados</h2>
        <p class="mt-2 text-[15px] leading-relaxed text-slate-500 dark:text-slate-400">
          Organiza tu trabajo en la jerarquía Proyecto · Sprint · WorkItem · Tarea,
          con tablero Kanban y seguimiento de estados.
        </p>
        <div class="mt-7 flex items-center justify-center gap-3">
          <a routerLink="/proyectos"
             class="inline-flex items-center gap-2 rounded-xl bg-ink-900 px-4 py-2.5 text-[14px] font-semibold text-white shadow-sm transition hover:bg-ink-800 dark:bg-brand-600 dark:hover:bg-brand-500">
            Ver proyectos
            <svg viewBox="0 0 24 24" fill="none" class="size-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14" /><path d="m12 5 7 7-7 7" /></svg>
          </a>
          <a routerLink="/usuarios"
             class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-[14px] font-semibold text-slate-700 transition hover:bg-slate-50 dark:border-dark-border dark:bg-dark-card dark:text-slate-200 dark:hover:bg-dark-sidebar">
            Usuarios
          </a>
        </div>
      </div>
    </div>
  `,
})
export class Home {
  private readonly shell = inject(ShellService);

  constructor() {
    this.shell.set('Inicio', [{ label: 'Inicio' }]);
  }
}
