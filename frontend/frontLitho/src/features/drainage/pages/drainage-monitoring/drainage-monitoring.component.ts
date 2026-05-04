import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {
  DRAINAGE_SIDE_LABELS,
  DRAINAGE_STATUS_LABELS,
  DRAINAGE_TYPE_LABELS,
  DrainageResponse,
} from '../../models/drainage.model';
import { DrainageService } from '../../services/drainage.service';

type Tab = 'OVERDUE' | 'ACTIVE' | 'REMOVED';

@Component({
  selector: 'app-drainage-monitoring',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './drainage-monitoring.component.html',
  styleUrl: './drainage-monitoring.component.scss'
})
export class DrainageMonitoringComponent implements OnInit {
  private drainageService = inject(DrainageService);
  private router = inject(Router);

  readonly typeLabels = DRAINAGE_TYPE_LABELS;
  readonly sideLabels = DRAINAGE_SIDE_LABELS;

  tab: Tab = 'OVERDUE';
  drainages: DrainageResponse[] = [];
  loading = false;
  error: string | null = null;

  overdueCount = 0;
  activeCount = 0;

  ngOnInit(): void {
    this.loadCounters();
    this.load();
  }

  setTab(tab: Tab): void {
    this.tab = tab;
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    const obs = this.tab === 'OVERDUE'
      ? this.drainageService.list({ overdue: true, status: 'ACTIVE' })
      : this.tab === 'ACTIVE'
        ? this.drainageService.list({ status: 'ACTIVE' })
        : this.drainageService.list({ status: 'REMOVED' });

    obs.subscribe({
      next: (list) => {
        this.drainages = list;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = err.status === 403
          ? "Vous n'avez pas l'autorisation de suivre les drainages."
          : (err.error?.message || 'Échec du chargement des drainages.');
      }
    });
  }

  private loadCounters(): void {
    this.drainageService.list({ status: 'ACTIVE' }).subscribe({
      next: (list) => {
        this.activeCount = list.length;
        this.overdueCount = list.filter(d => d.overdue).length;
      },
      error: () => {}
    });
  }

  open(d: DrainageResponse): void {
    this.router.navigate(['/drainages', d.id]);
  }

  statusClass(d: DrainageResponse): string {
    if (d.status === 'REMOVED') return 'removed';
    if (d.overdue) return 'overdue';
    return 'active';
  }

  statusLabel(d: DrainageResponse): string {
    if (d.status === 'ACTIVE' && d.overdue) return DRAINAGE_STATUS_LABELS.OVERDUE;
    return DRAINAGE_STATUS_LABELS[d.status];
  }

  daysUntil(date: string | null): number | null {
    if (!date) return null;
    const target = new Date(date + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const diff = target.getTime() - today.getTime();
    return Math.round(diff / (1000 * 60 * 60 * 24));
  }
}
