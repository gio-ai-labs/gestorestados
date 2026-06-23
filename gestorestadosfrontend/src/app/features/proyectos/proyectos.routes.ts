import { Routes } from '@angular/router';

export const PROYECTOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./proyectos-list/proyectos-list').then((m) => m.ProyectosList),
  },
  {
    path: ':proyectoId/sprints',
    loadChildren: () => import('../sprints/sprints.routes').then((m) => m.SPRINTS_ROUTES),
  },
];

export default PROYECTOS_ROUTES;
