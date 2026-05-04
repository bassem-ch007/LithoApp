import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from './services/notification.service';
import {
  AppNotification,
  NOTIFICATION_TYPE_LABELS,
  NotificationReferenceType
} from './models/notification.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss'
})
export class NotificationsComponent implements OnInit {
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly typeLabels = NOTIFICATION_TYPE_LABELS;

  notifications: AppNotification[] = [];
  loading = false;
  error: string | null = null;
  page = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  ngOnInit(): void {
    this.load(0);
  }

  load(page: number): void {
    this.loading = true;
    this.error = null;
    this.notificationService.list(page, this.pageSize).subscribe({
      next: res => {
        this.notifications = res.content;
        this.page = res.number;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = 'Erreur lors du chargement des notifications';
      }
    });
  }

  onClick(notification: AppNotification): void {
    if (notification.status !== 'READ') {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: updated => {
          notification.status = updated.status;
          notification.readAt = updated.readAt;
          this.notificationService.refreshUnreadCount();
        },
        error: () => {
          // Mark-read failure is non-blocking; still navigate.
        }
      });
    }
    this.navigateTo(notification);
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        const now = new Date().toISOString();
        this.notifications.forEach(n => {
          if (n.status !== 'READ') {
            n.status = 'READ';
            n.readAt = now;
          }
        });
        this.notificationService.refreshUnreadCount();
      },
      error: () => {
        this.error = 'Erreur lors du marquage des notifications';
      }
    });
  }

  refresh(): void {
    this.load(this.page);
    this.notificationService.refreshUnreadCount();
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) this.load(this.page + 1);
  }

  prevPage(): void {
    if (this.page > 0) this.load(this.page - 1);
  }

  trackById(_: number, n: AppNotification): string {
    return n.id;
  }

  private navigateTo(n: AppNotification): void {
    const target = this.routeFor(n.referenceType, n.referenceId);
    if (target) {
      this.router.navigateByUrl(target).catch(() => {
        // Route may not exist for the current role — fail silently, user stays on the list.
      });
    }
  }

  private routeFor(type: NotificationReferenceType | null, id: string | null): string | null {
    if (!type || !id) return null;
    switch (type) {
      case 'ANALYSIS':
        return `/analysis-requests/${id}`;
      case 'DRAINAGE':
        return `/drainages/${id}`;
      case 'PATIENT':
        return `/patients/${id}`;
      case 'EPISODE':
        return `/episodes/${id}`;
      default:
        return null;
    }
  }
}
