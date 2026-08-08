import { ChangeDetectionStrategy, Component, input, model } from '@angular/core';

/**
 * Modal centrado para formularios CRUD. Two-way [(open)]. Cierra con backdrop o Escape.
 * Slots: contenido por defecto + [modal-footer] para acciones.
 */
@Component({
  selector: 'ui-modal',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './modal.css',
  host: {
    '(keydown.escape)': 'cerrar()',
  },
  template: `
    @if (open()) {
      <div class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto p-4 sm:p-6">
        <!-- backdrop -->
        <div
          class="ui-backdrop fixed inset-0 bg-ink-900/40 backdrop-blur-[2px] dark:bg-black/60"
          (click)="cerrar()"></div>

        <!-- panel -->
        <div
          class="ui-panel relative z-10 mt-8 w-full max-w-lg rounded-2xl border border-slate-200 bg-white shadow-xl shadow-ink-900/10 dark:border-dark-border dark:bg-dark-card dark:shadow-black/30"
          role="dialog"
          aria-modal="true">
          <div class="flex items-start justify-between gap-4 border-b border-slate-100 px-6 py-4 dark:border-dark-border">
            <div>
              <h2 class="font-display text-[17px] font-bold text-ink-900 dark:text-slate-100">{{ titulo() }}</h2>
              @if (subtitulo()) {
                <p class="mt-0.5 text-[13px] text-slate-500 dark:text-slate-400">{{ subtitulo() }}</p>
              }
            </div>
            <button
              type="button"
              (click)="cerrar()"
              class="grid size-8 shrink-0 place-items-center rounded-lg text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 dark:text-slate-500 dark:hover:bg-dark-sidebar dark:hover:text-slate-300"
              aria-label="Cerrar">
              <svg viewBox="0 0 24 24" fill="none" class="size-5" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12" /></svg>
            </button>
          </div>

          <div class="px-6 py-5">
            <ng-content />
          </div>

          <div class="flex items-center justify-end gap-3 border-t border-slate-100 bg-slate-50/50 px-6 py-4 rounded-b-2xl dark:border-dark-border dark:bg-dark-sidebar/50">
            <ng-content select="[modal-footer]" />
          </div>
        </div>
      </div>
    }
  `,
})
export class Modal {
  readonly open = model<boolean>(false);
  readonly titulo = input<string>('');
  readonly subtitulo = input<string>();

  protected cerrar(): void {
    this.open.set(false);
  }
}
