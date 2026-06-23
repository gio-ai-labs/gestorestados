import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { mensajeDeError } from './problem-detail.util';

/**
 * Interceptor funcional de errores. Notifica globalmente los fallos de infraestructura
 * (sin conexion o 5xx) via Toast; los 4xx se re-lanzan para que cada feature los maneje
 * inline (p. ej. errores de validacion en formularios). Siempre re-lanza el error.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toast = inject(ToastService);
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 0 || err.status >= 500) {
        toast.error(mensajeDeError(err));
      }
      return throwError(() => err);
    }),
  );
};
