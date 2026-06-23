import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ButtonDirective } from '../button/button';
import { ConfirmService } from './confirm.service';

/** Host del dialogo de confirmacion. Montar una sola vez (AppShell). */
@Component({
  selector: 'ui-confirm-host',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ButtonDirective],
  host: {
    '(keydown.escape)': 'confirm.responder(false)',
  },
  template: `
    @if (estado(); as e) {
      <div class="fixed inset-0 z-[70] flex items-center justify-center p-4">
        <div class="ui-backdrop fixed inset-0 bg-ink-900/40 backdrop-blur-[2px]" (click)="confirm.responder(false)"></div>
        <div class="ui-panel relative z-10 w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-xl shadow-ink-900/10" role="alertdialog" aria-modal="true">
          <div class="flex gap-4">
            <span
              class="grid size-10 shrink-0 place-items-center rounded-full"
              [class]="e.peligro ? 'bg-rose-50 text-rose-600' : 'bg-brand-50 text-brand-600'">
              <svg viewBox="0 0 24 24" fill="none" class="size-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 9v4m0 4h.01M10.3 3.9 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0Z" />
              </svg>
            </span>
            <div class="min-w-0">
              <h2 class="font-display text-[16px] font-bold text-ink-900">{{ e.titulo }}</h2>
              <p class="mt-1 text-[14px] leading-relaxed text-slate-500">{{ e.mensaje }}</p>
            </div>
          </div>
          <div class="mt-6 flex items-center justify-end gap-3">
            <button uiButton variant="secondary" size="sm" type="button" (click)="confirm.responder(false)">
              {{ e.cancelar ?? 'Cancelar' }}
            </button>
            <button uiButton [variant]="e.peligro ? 'danger' : 'primary'" size="sm" type="button" (click)="confirm.responder(true)">
              {{ e.confirmar ?? 'Confirmar' }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .ui-backdrop { animation: ui-fade 0.16s ease-out; }
      .ui-panel { animation: ui-pop 0.18s cubic-bezier(0.16, 1, 0.3, 1); }
      @keyframes ui-fade { from { opacity: 0; } }
      @keyframes ui-pop { from { opacity: 0; transform: translateY(8px) scale(0.98); } }
    `,
  ],
})
export class ConfirmHost {
  protected readonly confirm = inject(ConfirmService);
  protected readonly estado = computed(() => this.confirm.estado());
}
