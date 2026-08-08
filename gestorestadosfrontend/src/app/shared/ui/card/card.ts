import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Card de seccion (interna). Soporta titulo/subtitulo opcionales y proyeccion de
 * acciones (slot [card-actions]) y contenido.
 */
@Component({
  selector: 'ui-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (titulo() || subtitulo()) {
      <div class="flex items-start justify-between gap-4 px-5 pt-5">
        <div>
          @if (titulo()) {
            <h3 class="font-display text-[15px] font-bold text-ink-900 dark:text-slate-100">{{ titulo() }}</h3>
          }
          @if (subtitulo()) {
            <p class="mt-0.5 text-[13px] text-slate-500 dark:text-slate-400">{{ subtitulo() }}</p>
          }
        </div>
        <ng-content select="[card-actions]" />
      </div>
    }
    <div class="p-5">
      <ng-content />
    </div>
  `,
  host: {
    class: 'block rounded-xl border border-slate-200/80 bg-white dark:border-dark-border dark:bg-dark-card',
  },
})
export class Card {
  readonly titulo = input<string>();
  readonly subtitulo = input<string>();
}
