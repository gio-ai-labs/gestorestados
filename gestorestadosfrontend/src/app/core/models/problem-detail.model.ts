/**
 * Forma de error homogenea del backend (RFC 7807 / ProblemDetail).
 * El campo `errors` aparece en validaciones de campos (400).
 */
export interface ProblemDetail {
  type?: string;
  title?: string;
  status: number;
  detail?: string;
  instance?: string;
  timestamp?: string;
  errors?: Record<string, string>;
}
