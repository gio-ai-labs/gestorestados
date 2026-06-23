import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Estado vacio reutilizable: icono + titulo + mensaje + slot de accion ([empty-action]).
 */
@Component({
  selector: 'ui-empty-state',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="grid place-items-center px-6 py-16 text-center">
      <div class="mb-4 grid size-12 place-items-center rounded-2xl bg-slate-100 text-slate-400">
        <svg viewBox="0 0 24 24" fill="none" class="size-6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
          <path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7Z" />
        </svg>
      </div>
      <h3 class="font-display text-[16px] font-bold text-ink-900">{{ titulo() }}</h3>
      @if (mensaje()) {
        <p class="mt-1 max-w-sm text-[14px] text-slate-500">{{ mensaje() }}</p>
      }
      <div class="mt-5">
        <ng-content select="[empty-action]" />
      </div>
    </div>
  `,
})
export class EmptyState {
  readonly titulo = input.required<string>();
  readonly mensaje = input<string>();
}
