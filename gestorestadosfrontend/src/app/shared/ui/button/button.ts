import { computed, Directive, input } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
export type ButtonSize = 'sm' | 'md';

/**
 * Directiva de boton: estilos consistentes sobre &lt;button&gt;/&lt;a&gt; nativos.
 * Uso: &lt;button uiButton variant="primary"&gt;Guardar&lt;/button&gt;
 */
@Directive({
  selector: '[uiButton]',
  host: {
    '[class]': 'clases()',
  },
})
export class ButtonDirective {
  readonly variant = input<ButtonVariant>('primary');
  readonly size = input<ButtonSize>('md');

  private static readonly BASE =
    'inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition ' +
    'focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-400 focus-visible:ring-offset-2 ' +
    'disabled:cursor-not-allowed disabled:opacity-50 select-none whitespace-nowrap';

  private static readonly SIZES: Record<ButtonSize, string> = {
    sm: 'px-3 py-1.5 text-[13px]',
    md: 'px-4 py-2.5 text-[14px]',
  };

  private static readonly VARIANTS: Record<ButtonVariant, string> = {
    primary: 'bg-ink-900 text-white shadow-sm hover:bg-ink-800',
    secondary: 'border border-slate-200 bg-white text-slate-700 hover:bg-slate-50',
    ghost: 'text-slate-600 hover:bg-slate-100 hover:text-slate-900',
    danger: 'bg-rose-600 text-white shadow-sm hover:bg-rose-700',
  };

  protected readonly clases = computed(
    () =>
      `${ButtonDirective.BASE} ${ButtonDirective.SIZES[this.size()]} ${ButtonDirective.VARIANTS[this.variant()]}`,
  );
}
