import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { Toast, ToastService, ToastTipo } from './toast.service';

/** Contenedor de toasts. Montar una sola vez (AppShell). */
@Component({
  selector: 'ui-toast-host',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './toast-host.css',
  template: `
    <div class="pointer-events-none fixed top-4 right-4 z-[60] flex w-[340px] max-w-[calc(100vw-2rem)] flex-col gap-2">
      @for (t of toasts(); track t.id) {
        <div
          class="ui-toast pointer-events-auto flex items-start gap-3 rounded-xl border bg-white px-4 py-3 shadow-lg shadow-ink-900/5 dark:bg-dark-card dark:shadow-black/20"
          [class]="borde(t.tipo)">
          <span class="mt-0.5 grid size-5 shrink-0 place-items-center rounded-full" [class]="iconoBg(t.tipo)">
            <svg viewBox="0 0 24 24" fill="none" class="size-3.5 text-white" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
              @switch (t.tipo) {
                @case ('exito') { <path d="M20 6 9 17l-5-5" /> }
                @case ('error') { <path d="M18 6 6 18M6 6l12 12" /> }
                @default { <path d="M12 8v4m0 4h.01" /> }
              }
            </svg>
          </span>
          <p class="flex-1 text-[13px] font-medium leading-snug text-slate-700 dark:text-slate-200">{{ t.mensaje }}</p>
          <button
            type="button"
            (click)="toast.descartar(t.id)"
            class="grid size-5 shrink-0 place-items-center rounded text-slate-300 transition hover:text-slate-500 dark:text-slate-600 dark:hover:text-slate-400"
            aria-label="Cerrar">
            <svg viewBox="0 0 24 24" fill="none" class="size-3.5" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12" /></svg>
          </button>
        </div>
      }
    </div>
  `,
})
export class ToastHost {
  protected readonly toast = inject(ToastService);
  protected readonly toasts = computed(() => this.toast.toasts());

  protected borde(tipo: ToastTipo): string {
    switch (tipo) {
      case 'exito':
        return 'border-teal-200 dark:border-teal-800';
      case 'error':
        return 'border-rose-200 dark:border-rose-800';
      default:
        return 'border-slate-200 dark:border-dark-border';
    }
  }

  protected iconoBg(tipo: ToastTipo): string {
    switch (tipo) {
      case 'exito':
        return 'bg-teal-500';
      case 'error':
        return 'bg-rose-500';
      default:
        return 'bg-slate-400';
    }
  }
}
