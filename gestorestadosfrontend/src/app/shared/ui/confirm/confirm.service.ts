import { Injectable, signal } from '@angular/core';

export interface ConfirmOptions {
  titulo: string;
  mensaje: string;
  confirmar?: string;
  cancelar?: string;
  peligro?: boolean;
}

interface ConfirmState extends ConfirmOptions {
  resolve: (v: boolean) => void;
}

/**
 * Confirmacion modal basada en promesa. Uso:
 *   if (await confirm.ask({ titulo, mensaje, peligro: true })) { ... }
 * El host se monta una sola vez en el AppShell.
 */
@Injectable({ providedIn: 'root' })
export class ConfirmService {
  readonly estado = signal<ConfirmState | null>(null);

  ask(opts: ConfirmOptions): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.estado.set({ ...opts, resolve });
    });
  }

  responder(valor: boolean): void {
    const actual = this.estado();
    if (actual) {
      actual.resolve(valor);
      this.estado.set(null);
    }
  }
}
