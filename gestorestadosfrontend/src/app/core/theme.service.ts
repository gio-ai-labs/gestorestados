import { Injectable, computed, effect, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

const STORAGE_KEY = 'gio-theme';

function resolveInitialTheme(): Theme {
  const stored = localStorage.getItem(STORAGE_KEY) as Theme | null;
  if (stored === 'light' || stored === 'dark') return stored;
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

/**
 * Gestiona el tema claro/oscuro de la aplicacion.
 * Persiste en localStorage y respeta prefers-color-scheme del SO como fallback.
 * Aplica/quita la clase .dark en <html> para activar las variantes dark: de Tailwind.
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly _theme = signal<Theme>(resolveInitialTheme());

  readonly isDark = computed(() => this._theme() === 'dark');

  constructor() {
    // Aplicar sincronamente para minimizar el flash al recargar
    document.documentElement.classList.toggle('dark', this._theme() === 'dark');

    effect(() => {
      const theme = this._theme();
      document.documentElement.classList.toggle('dark', theme === 'dark');
      localStorage.setItem(STORAGE_KEY, theme);
    });
  }

  toggle(): void {
    this._theme.update(t => (t === 'dark' ? 'light' : 'dark'));
  }
}
