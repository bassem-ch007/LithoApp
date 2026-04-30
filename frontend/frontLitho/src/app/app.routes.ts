import { Routes } from '@angular/router';
import { appAccessGuard } from './core/auth/app-access.guard';
import { authGuard } from './core/auth/auth.guard';
import { dashboardRedirectGuard, dashboardRedirectMatchGuard } from './core/auth/dashboard-redirect.guard';
import { pendingApprovalGuard } from './core/auth/pending-approval.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    canActivateChild: [appAccessGuard],
    loadComponent: () =>
      import('../layout/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        canMatch: [dashboardRedirectMatchGuard],
        canActivate: [dashboardRedirectGuard],
        loadComponent: () =>
          import('../features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'urologist/dashboard',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/urologist-dashboard/urologist-dashboard.component').then(m => m.UrologistDashboardComponent)
      },
      {
        path: 'biologist/dashboard',
        canActivate: [roleGuard],
        data: { roles: ['BIOLOGIST'] },
        loadComponent: () =>
          import('../features/biologist-dashboard/biologist-dashboard.component').then(m => m.BiologistDashboardComponent)
      },
      {
        path: 'admin/dashboard',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('../features/admin/admin.component').then(m => m.AdminComponent)
      },
      {
        path: 'pending-approval',
        canActivate: [pendingApprovalGuard],
        loadComponent: () =>
          import('../features/pending-approval/pending-approval.component').then(m => m.PendingApprovalComponent)
      },
      {
        path: 'patients',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/patients/patients.component').then(m => m.PatientsComponent)
      },
      {
        path: 'episodes',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/episodes/episodes.component').then(m => m.EpisodesComponent)
      },
      {
        path: 'analyses',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE', 'BIOLOGIST'] },
        loadComponent: () =>
          import('../features/analyses/analyses.component').then(m => m.AnalysesComponent)
      },
      {
        path: 'analysis-requests',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE', 'BIOLOGIST'] },
        loadComponent: () =>
          import('../features/analyses/analyses.component').then(m => m.AnalysesComponent)
      },
      {
        path: 'analysis-search',
        canActivate: [roleGuard],
        data: { roles: ['BIOLOGIST'] },
        loadComponent: () =>
          import('../features/analyses/analyses.component').then(m => m.AnalysesComponent)
      },
      {
        path: 'analysis-results',
        canActivate: [roleGuard],
        data: { roles: ['BIOLOGIST'] },
        loadComponent: () =>
          import('../features/analyses/analyses.component').then(m => m.AnalysesComponent)
      },
      {
        path: 'drainage',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/drainage/drainage.component').then(m => m.DrainageComponent)
      },
      {
        path: 'access-denied',
        loadComponent: () =>
          import('../features/access-denied/access-denied.component').then(m => m.AccessDeniedComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
