import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';

/**
 * Control de paginación presentacional (tonto): muestra el rango de elementos
 * visibles, la página actual y total, y permite navegar a la página anterior o
 * siguiente. No conoce el origen de los datos; recibe el estado de página y
 * emite el índice (base 0) de la página solicitada vía `paginaCambia`.
 *
 * Uso:
 *   <ui-paginacion
 *     [pagina]="pagina()"
 *     [totalPaginas]="totalPaginas()"
 *     [totalElementos]="totalElementos()"
 *     [tamano]="tamano()"
 *     (paginaCambia)="irA($event)" />
 */
@Component({
  selector: 'ui-paginacion',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './paginacion.html',
  styleUrl: './paginacion.css',
})
export class Paginacion {
  /** Página actual (base 0). */
  readonly pagina = input.required<number>();
  /** Total de páginas disponibles. */
  readonly totalPaginas = input.required<number>();
  /** Total de elementos en todas las páginas. */
  readonly totalElementos = input.required<number>();
  /** Tamaño de página (elementos por página). */
  readonly tamano = input.required<number>();

  /** Emite el índice (base 0) de la página solicitada. */
  readonly paginaCambia = output<number>();

  protected readonly enPrimera = computed(() => this.pagina() <= 0);
  protected readonly enUltima = computed(() => this.pagina() >= this.totalPaginas() - 1);

  /** Índice (base 1) del primer elemento de la página actual. */
  protected readonly desde = computed(() =>
    this.totalElementos() === 0 ? 0 : this.pagina() * this.tamano() + 1,
  );

  /** Índice (base 1) del último elemento de la página actual. */
  protected readonly hasta = computed(() =>
    Math.min((this.pagina() + 1) * this.tamano(), this.totalElementos()),
  );

  protected anterior(): void {
    if (!this.enPrimera()) {
      this.paginaCambia.emit(this.pagina() - 1);
    }
  }

  protected siguiente(): void {
    if (!this.enUltima()) {
      this.paginaCambia.emit(this.pagina() + 1);
    }
  }
}
