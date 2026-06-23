import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ShellService } from '../shell.service';
import { ToastHost } from '../../shared/ui/toast/toast-host';
import { ConfirmHost } from '../../shared/ui/confirm/confirm-host';

interface NavItem {
  label: string;
  link: string;
  icon: 'proyectos' | 'sprints' | 'usuarios';
}

/**
 * AppShell: sidebar full-height + topbar (breadcrumb + titulo) + card de contenido.
 * Estructura segun estructura.jpeg (§8.3 del plan).
 */
@Component({
  selector: 'app-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastHost, ConfirmHost],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.css',
})
export class AppShell {
  protected readonly shell = inject(ShellService);

  protected readonly nav = signal<NavItem[]>([
    { label: 'Proyectos', link: '/proyectos', icon: 'proyectos' },
    { label: 'Sprints', link: '/sprints', icon: 'sprints' },
    { label: 'Usuarios', link: '/usuarios', icon: 'usuarios' },
  ]);
}
