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
        redirectTo: 'patients',
        pathMatch: 'full'
      },
      {
        path: 'biologist/dashboard',
        redirectTo: 'analysis-search',
        pathMatch: 'full'
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
          import('../features/patients/patients.component').then(m => m.PatientsComponent),
        children: [
          {
            path: '',
            loadComponent: () =>
              import('../features/patients/pages/patient-list/patient-list.component').then(m => m.PatientListComponent)
          },
          {
            path: 'new',
            loadComponent: () =>
              import('../features/patients/pages/patient-form/patient-form.component').then(m => m.PatientFormComponent)
          },
          {
            path: ':patientId/episodes',
            loadComponent: () =>
              import('../features/episodes/pages/episode-list/episode-list.component').then(m => m.EpisodeListComponent)
          },
          {
            path: ':patientId/episodes/new',
            loadComponent: () =>
              import('../features/episodes/pages/episode-form/episode-form.component').then(m => m.EpisodeFormComponent)
          },
          {
            path: ':id',
            loadComponent: () =>
              import('../features/patients/pages/patient-details/patient-details.component').then(m => m.PatientDetailsComponent)
          },
          {
            path: ':id/edit',
            loadComponent: () =>
              import('../features/patients/pages/patient-form/patient-form.component').then(m => m.PatientFormComponent)
          }
        ]
      },
      {
        path: 'episodes/:episodeId',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/episodes/pages/episode-details/episode-details.component').then(m => m.EpisodeDetailsComponent)
      },
      {
        path: 'episodes/:episodeId/edit',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/episodes/pages/episode-form/episode-form.component').then(m => m.EpisodeFormComponent)
      },
      {
        path: 'episodes',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/episodes/episodes.component').then(m => m.EpisodesComponent)
      },
      {
        path: 'episodes/:episodeId/analyses',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/analyses/pages/analysis-list/analysis-list.component').then(m => m.AnalysisListComponent)
      },
      {
        path: 'episodes/:episodeId/analyses/new',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/analyses/pages/analysis-create/analysis-create.component').then(m => m.AnalysisCreateComponent)
      },
      {
        path: 'analysis-requests/:id',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE', 'BIOLOGIST', 'ADMIN'] },
        loadComponent: () =>
          import('../features/analyses/pages/analysis-details/analysis-details.component').then(m => m.AnalysisDetailsComponent)
      },
      {
        path: 'analysis-requests',
        redirectTo: 'patients',
        pathMatch: 'full'
      },
      {
        path: 'analysis-search',
        canActivate: [roleGuard],
        data: { roles: ['BIOLOGIST'] },
        loadComponent: () =>
          import('../features/analyses/pages/analysis-search/analysis-search.component').then(m => m.AnalysisSearchComponent)
      },
      {
        path: 'analysis-results',
        redirectTo: 'analysis-search',
        pathMatch: 'full'
      },
      {
        path: 'drainage',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/drainage/pages/drainage-monitoring/drainage-monitoring.component').then(m => m.DrainageMonitoringComponent)
      },
      {
        path: 'episodes/:episodeId/drainages',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/drainage/pages/drainage-list/drainage-list.component').then(m => m.DrainageListComponent)
      },
      {
        path: 'episodes/:episodeId/drainages/new',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/drainage/pages/drainage-create/drainage-create.component').then(m => m.DrainageCreateComponent)
      },
      {
        path: 'patients/:patientId/drainages',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE'] },
        loadComponent: () =>
          import('../features/drainage/pages/drainage-list/drainage-list.component').then(m => m.DrainageListComponent)
      },
      {
        path: 'drainages/:id',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE', 'ADMIN'] },
        loadComponent: () =>
          import('../features/drainage/pages/drainage-details/drainage-details.component').then(m => m.DrainageDetailsComponent)
      },
      {
        path: 'notifications',
        canActivate: [roleGuard],
        data: { roles: ['UROLOGUE', 'BIOLOGIST', 'ADMIN'] },
        loadComponent: () =>
          import('../features/notifications/notifications.component').then(m => m.NotificationsComponent)
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
