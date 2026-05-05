import { Component, HostListener, OnDestroy, OnInit, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';
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
  private router = inject(Router);

  sidebarOpen = false;
  private routerSub?: Subscription;

  ngOnInit(): void {
    if (this.authService.isAuthenticated() && this.authService.hasAppRole()) {
      this.notificationService.startPolling(20_000);
    }
    this.routerSub = this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => this.closeSidebar());
  }

  ngOnDestroy(): void {
    this.notificationService.stopPolling();
    this.routerSub?.unsubscribe();
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  closeSidebar(): void {
    this.sidebarOpen = false;
  }

  @HostListener('window:resize')
  onResize(): void {
    if (window.innerWidth >= 992) {
      this.sidebarOpen = false;
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
