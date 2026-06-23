import { ChangeDetectionStrategy, Component } from '@angular/core';

/**
 * Contenedor de tabla con estilo consistente. El consumidor proyecta &lt;thead&gt;/&lt;tbody&gt;
 * usando las clases utilitarias de la app; las reglas base viven en styles.css (.ui-table).
 */
@Component({
  selector: 'ui-table',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="ui-table overflow-x-auto rounded-xl border border-slate-200/80">
      <table class="w-full border-collapse text-left text-[14px]">
        <ng-content />
      </table>
    </div>
  `,
})
export class Table {}
