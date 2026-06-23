import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** Indicador de carga centrado. */
@Component({
  selector: 'ui-spinner',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="grid place-items-center" [style.min-height.px]="alto()">
      <span class="inline-block size-7 animate-spin rounded-full border-[3px] border-slate-200 border-t-brand-600"></span>
    </div>
  `,
})
export class Spinner {
  readonly alto = input<number>(160);
}
