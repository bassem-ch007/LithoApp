import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async (_route, state): Promise<boolean | UrlTree> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isInitialized() && authService.isAuthenticated()) {
    return true;
  }

  await authService.login(window.location.origin + state.url);
  return router.parseUrl('/');
};
