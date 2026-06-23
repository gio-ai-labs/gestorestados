import { Injectable, signal } from '@angular/core';

export type ToastTipo = 'exito' | 'error' | 'info';

export interface Toast {
  id: number;
  tipo: ToastTipo;
  mensaje: string;
}

/** Notificaciones efimeras (top-right). El host se monta una vez en el AppShell. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private seq = 0;
  readonly toasts = signal<Toast[]>([]);

  exito(mensaje: string): void {
    this.push('exito', mensaje);
  }

  error(mensaje: string): void {
    this.push('error', mensaje);
  }

  info(mensaje: string): void {
    this.push('info', mensaje);
  }

  descartar(id: number): void {
    this.toasts.update((list) => list.filter((t) => t.id !== id));
  }

  private push(tipo: ToastTipo, mensaje: string): void {
    const id = ++this.seq;
    this.toasts.update((list) => [...list, { id, tipo, mensaje }]);
    setTimeout(() => this.descartar(id), 4200);
  }
}
