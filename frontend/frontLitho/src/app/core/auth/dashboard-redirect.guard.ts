import { CanActivateFn, CanMatchFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

const getDashboardRedirectUrl = (): UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const role = authService.getPrimaryAppRole();

  if (role === 'ADMIN') {
    return router.parseUrl('/admin/dashboard');
  }

  if (role === 'UROLOGUE') {
    return router.parseUrl('/urologist/dashboard');
  }

  if (role === 'BIOLOGIST') {
    return router.parseUrl('/biologist/dashboard');
  }

  return router.parseUrl('/pending-approval');
};

export const dashboardRedirectGuard: CanActivateFn = (): UrlTree => getDashboardRedirectUrl();

export const dashboardRedirectMatchGuard: CanMatchFn = (): UrlTree => getDashboardRedirectUrl();
