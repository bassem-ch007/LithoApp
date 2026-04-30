import { Component, inject } from '@angular/core';
import { AuthService } from '../../app/core/auth/auth.service';

@Component({
  selector: 'app-pending-approval',
  imports: [],
  templateUrl: './pending-approval.component.html',
  styleUrl: './pending-approval.component.scss'
})
export class PendingApprovalComponent {
  private authService = inject(AuthService);
  isChecking = false;

  async checkAgain(): Promise<void> {
    this.isChecking = true;
    await this.authService.refreshUserRolesOrToken();
    this.isChecking = false;
    this.authService.redirectByRole();
  }

  logout(): void {
    this.authService.logout();
  }
}
