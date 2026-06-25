/**
 * Respuesta paginada genérica del backend. Coincide con el contrato de los
 * endpoints paginados: el índice de página se expone como `page` (base 0).
 */
export interface Pagina<T> {
  readonly content: T[];
  readonly totalElements: number;
  readonly totalPages: number;
  readonly page: number;
  readonly size: number;
}
