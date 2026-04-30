import { ActivatedRouteSnapshot, CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data['roles'] as string[] | undefined;

  if (!expectedRoles || expectedRoles.length === 0) {
    return true;
  }

  if (authService.hasAnyRole(expectedRoles)) {
    return true;
  }

  if (!authService.hasAppRole()) {
    return router.parseUrl('/pending-approval');
  }

  return router.parseUrl('/access-denied');
};
