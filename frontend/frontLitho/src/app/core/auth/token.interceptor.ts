import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { from, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (!req.url.startsWith(environment.apiBaseUrl)) {
    return next(req);
  }

  if (!authService.isAuthenticated()) {
    return next(req);
  }

  return from(authService.refreshToken(30)).pipe(
    switchMap(() => {
      const token = authService.getToken();

      const authReq = token
        ? req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        })
        : req;

      return next(authReq);
    }),
    catchError((error) => {
      console.error('Token refresh failed', error);
      return next(req);
    })
  );
};
