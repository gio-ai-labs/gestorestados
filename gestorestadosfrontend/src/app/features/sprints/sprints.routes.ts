import { Routes } from '@angular/router';

export const SPRINTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./sprints-list/sprints-list').then((m) => m.SprintsList),
  },
  {
    path: ':sprintId/tablero',
    loadComponent: () =>
      import('./tablero-sprint/tablero-sprint').then((m) => m.TableroSprint),
  },
];

export default SPRINTS_ROUTES;
