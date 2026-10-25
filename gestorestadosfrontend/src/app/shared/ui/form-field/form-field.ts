import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Campo de formulario: etiqueta (+ asterisco si requerido), control proyectado y
 * mensaje de error/ayuda. Pensado para reactive forms.
 */
@Component({
  selector: 'ui-form-field',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <label class="mb-1.5 block text-[13px] font-semibold text-slate-700 dark:text-slate-200">
      {{ label() }}
      @if (requerido()) {
        <span class="text-rose-500">*</span>
      }
    </label>
    <ng-content />
    @if (error()) {
      <p class="mt-1.5 text-[12px] font-medium text-rose-600">{{ error() }}</p>
    } @else if (ayuda()) {
      <p class="mt-1.5 text-[12px] text-slate-400 dark:text-slate-500">{{ ayuda() }}</p>
    }
  `,
  host: { class: 'block' },
})
export class FormField {
  readonly label = input.required<string>();
  readonly requerido = input<boolean>(false);
  readonly error = input<string | null>(null);
  readonly ayuda = input<string>();
}
