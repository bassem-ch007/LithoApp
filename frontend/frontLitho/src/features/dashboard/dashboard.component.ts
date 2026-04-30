import { Component, OnInit, inject } from '@angular/core';
import { AuthService } from '../../app/core/auth/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.authService.redirectByRole();
  }
}
