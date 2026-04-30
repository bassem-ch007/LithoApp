import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const pendingApprovalGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.hasAppRole()) {
    return true;
  }

  const primaryRole = authService.getPrimaryAppRole();

  if (primaryRole === 'ADMIN') {
    return router.parseUrl('/admin/dashboard');
  }

  if (primaryRole === 'UROLOGUE') {
    return router.parseUrl('/urologist/dashboard');
  }

  if (primaryRole === 'BIOLOGIST') {
    return router.parseUrl('/biologist/dashboard');
  }

  return router.parseUrl('/dashboard');
};
