import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { Estado, ESTADO_LABEL } from '../../../core/models';

/** Chip de estado con color por valor (PENDIENTE / EN_PROCESO / COMPLETADO). */
@Component({
  selector: 'ui-estado-chip',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span
      class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-[12px] font-semibold ring-1 ring-inset"
      [class]="clases()">
      <span class="size-1.5 rounded-full" [class]="dot()"></span>
      {{ label() }}
    </span>
  `,
})
export class EstadoChip {
  readonly estado = input.required<Estado>();

  protected readonly label = computed(() => ESTADO_LABEL[this.estado()]);

  protected readonly clases = computed(() => {
    switch (this.estado()) {
      case 'EN_PROCESO':
        return 'bg-brand-50 text-brand-700 ring-brand-200';
      case 'COMPLETADO':
        return 'bg-teal-50 text-teal-700 ring-teal-200';
      default:
        return 'bg-slate-100 text-slate-600 ring-slate-200';
    }
  });

  protected readonly dot = computed(() => {
    switch (this.estado()) {
      case 'EN_PROCESO':
        return 'bg-brand-500';
      case 'COMPLETADO':
        return 'bg-teal-500';
      default:
        return 'bg-slate-400';
    }
  });
}
