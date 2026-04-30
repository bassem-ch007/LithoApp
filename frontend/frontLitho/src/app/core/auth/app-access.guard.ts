import { ActivatedRouteSnapshot, CanActivateChildFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

const OPEN_CHILD_ROUTES = new Set(['pending-approval', 'access-denied', 'dashboard']);

export const appAccessGuard: CanActivateChildFn = (
  childRoute: ActivatedRouteSnapshot
): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const path = childRoute.routeConfig?.path ?? '';

  if (!authService.hasAppRole()) {
    return path === 'pending-approval' ? true : router.parseUrl('/pending-approval');
  }

  if (path === 'pending-approval') {
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

    return router.parseUrl('/dashboard');
  }

  if (OPEN_CHILD_ROUTES.has(path)) {
    return true;
  }

  const expectedRoles = childRoute.data['roles'] as string[] | undefined;

  if (!expectedRoles || expectedRoles.length === 0) {
    return true;
  }

  return authService.hasAnyRole(expectedRoles) ? true : router.parseUrl('/access-denied');
};
