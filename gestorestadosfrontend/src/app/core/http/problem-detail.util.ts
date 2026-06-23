import { HttpErrorResponse } from '@angular/common/http';
import { ProblemDetail } from '../models';

/**
 * Extrae un mensaje legible para el usuario a partir de un error HTTP del backend
 * (que responde con ProblemDetail). Concatena errores de validacion por campo si existen.
 */
/**
 * Devuelve los errores de validacion por campo (400 con ProblemDetail.errors), o null.
 */
export function erroresDeCampo(err: unknown): Record<string, string> | null {
  if (err instanceof HttpErrorResponse && err.status === 400) {
    const pd = err.error as ProblemDetail | undefined;
    if (pd?.errors && Object.keys(pd.errors).length) {
      return pd.errors;
    }
  }
  return null;
}

export function mensajeDeError(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    const pd = err.error as ProblemDetail | undefined;
    if (pd && typeof pd === 'object') {
      if (pd.errors) {
        const campos = Object.values(pd.errors);
        if (campos.length) {
          return campos.join('. ');
        }
      }
      if (pd.detail) {
        return pd.detail;
      }
      if (pd.title) {
        return pd.title;
      }
    }
    if (err.status === 0) {
      return 'No se pudo conectar con el servidor.';
    }
    return `Error ${err.status}.`;
  }
  return 'Ocurrio un error inesperado.';
}
