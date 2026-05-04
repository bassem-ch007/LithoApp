import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, Subscription, catchError, of, switchMap, timer } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AppNotification,
  NotificationPage,
  UnreadCountResponse
} from '../models/notification.model';
import { AuthService } from '../../../app/core/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly baseUrl = `${environment.apiBaseUrl}/notifications`;

  private readonly unreadCount$ = new BehaviorSubject<number>(0);
  private pollSubscription: Subscription | null = null;

  /** Observable of the current unread count, refreshed by the poller. */
  readonly unreadCount = this.unreadCount$.asObservable();

  list(page = 0, size = 20): Observable<NotificationPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<NotificationPage>(this.baseUrl, { params });
  }

  getById(id: string): Observable<AppNotification> {
    return this.http.get<AppNotification>(`${this.baseUrl}/${id}`);
  }

  getUnreadCount(): Observable<UnreadCountResponse> {
    return this.http.get<UnreadCountResponse>(`${this.baseUrl}/unread-count`);
  }

  markAsRead(id: string): Observable<AppNotification> {
    return this.http.put<AppNotification>(`${this.baseUrl}/${id}/read`, {});
  }

  markAllAsRead(): Observable<UnreadCountResponse> {
    return this.http.put<UnreadCountResponse>(`${this.baseUrl}/read-all`, {});
  }

  /**
   * Start polling the unread count every {intervalMs} milliseconds.
   * Safe to call multiple times — only one poller is active at a time.
   * Stops automatically when the user is no longer authenticated.
   */
  startPolling(intervalMs = 20_000): void {
    if (this.pollSubscription) {
      return;
    }
    this.pollSubscription = timer(0, intervalMs)
      .pipe(
        switchMap(() => {
          if (!this.authService.isAuthenticated() || !this.authService.hasAppRole()) {
            return of<UnreadCountResponse>({ unread: 0 });
          }
          return this.getUnreadCount().pipe(
            catchError(() => of<UnreadCountResponse>({ unread: this.unreadCount$.value }))
          );
        })
      )
      .subscribe(res => this.unreadCount$.next(res.unread));
  }

  stopPolling(): void {
    this.pollSubscription?.unsubscribe();
    this.pollSubscription = null;
  }

  /** Local optimistic update — call after a successful markAsRead/markAllAsRead. */
  refreshUnreadCount(): void {
    this.getUnreadCount()
      .pipe(catchError(() => of<UnreadCountResponse>({ unread: this.unreadCount$.value })))
      .subscribe(res => this.unreadCount$.next(res.unread));
  }
}
