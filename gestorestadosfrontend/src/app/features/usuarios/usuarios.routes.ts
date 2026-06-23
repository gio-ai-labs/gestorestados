import { Routes } from '@angular/router';

export const USUARIOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./usuarios-list/usuarios-list').then((m) => m.UsuariosList),
  },
];

export default USUARIOS_ROUTES;
