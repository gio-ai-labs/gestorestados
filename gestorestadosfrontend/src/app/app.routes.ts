import { Routes } from '@angular/router';
import { AppShell } from './layout/app-shell/app-shell';

export const routes: Routes = [
  {
    path: '',
    component: AppShell,
    children: [
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () => import('./features/home/home').then((m) => m.Home),
      },
      {
        path: 'proyectos',
        loadChildren: () => import('./features/proyectos/proyectos.routes').then((m) => m.PROYECTOS_ROUTES),
      },
      {
        path: 'sprints',
        loadComponent: () => import('./features/sprints/sprints-list/sprints-list').then((m) => m.SprintsList),
      },
      {
        path: 'usuarios',
        loadChildren: () => import('./features/usuarios/usuarios.routes').then((m) => m.USUARIOS_ROUTES),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
