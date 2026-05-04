import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../app/core/auth/auth.service';
import { NotificationService } from '../../features/notifications/services/notification.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent implements OnInit, OnDestroy {
  authService = inject(AuthService);
  notificationService = inject(NotificationService);

  ngOnInit(): void {
    if (this.authService.isAuthenticated() && this.authService.hasAppRole()) {
      this.notificationService.startPolling(20_000);
    }
  }

  ngOnDestroy(): void {
    this.notificationService.stopPolling();
  }

  logout(): void {
    this.authService.logout();
  }
}
