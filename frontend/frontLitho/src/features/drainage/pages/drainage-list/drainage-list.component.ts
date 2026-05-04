import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  DRAINAGE_SIDE_LABELS,
  DRAINAGE_STATUS_LABELS,
  DRAINAGE_TYPE_LABELS,
  DrainageResponse,
  JJ_TYPE_LABELS
} from '../../models/drainage.model';
import { DrainageService } from '../../services/drainage.service';
import { EpisodeResponse } from '../../../episodes/models/episode.model';
import { EpisodeService } from '../../../episodes/services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';
import { AuthService } from '../../../../app/core/auth/auth.service';

@Component({
  selector: 'app-drainage-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './drainage-list.component.html',
  styleUrl: './drainage-list.component.scss'
})
export class DrainageListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private drainageService = inject(DrainageService);
  private episodeService = inject(EpisodeService);
  private patientService = inject(PatientService);
  authService = inject(AuthService);

  readonly typeLabels = DRAINAGE_TYPE_LABELS;
  readonly sideLabels = DRAINAGE_SIDE_LABELS;
  readonly jjLabels = JJ_TYPE_LABELS;

  scope: 'episode' | 'patient' = 'episode';
  episodeId: number | null = null;
  patientId: number | null = null;

  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  drainages: DrainageResponse[] = [];
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    const patientIdParam = this.route.snapshot.paramMap.get('patientId');

    if (episodeIdParam) {
      this.scope = 'episode';
      this.episodeId = +episodeIdParam;
      if (Number.isNaN(this.episodeId)) {
        this.error = "Route d'épisode invalide.";
        return;
      }
      this.loadEpisode();
      this.load();
    } else if (patientIdParam) {
      this.scope = 'patient';
      this.patientId = +patientIdParam;
      if (Number.isNaN(this.patientId)) {
        this.error = 'Route patient invalide.';
        return;
      }
      this.loadPatient(this.patientId);
      this.load();
    } else {
      this.error = 'Aucun contexte patient ou épisode.';
    }
  }

  load(): void {
    this.loading = true;
    this.error = null;
    const obs = this.scope === 'episode' && this.episodeId
      ? this.drainageService.listByEpisode(this.episodeId)
      : this.drainageService.listByPatient(this.patientId!);

    obs.subscribe({
      next: (list) => {
        this.drainages = list;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = err.status === 403
          ? "Vous n'avez pas l'autorisation de consulter les drainages."
          : (err.error?.message || 'Échec du chargement des drainages.');
      }
    });
  }

  private loadEpisode(): void {
    if (!this.episodeId) return;
    this.episodeService.getEpisodeById(this.episodeId).subscribe({
      next: (e) => {
        this.episode = e;
        this.loadPatient(e.patientId);
      },
      error: () => {}
    });
  }

  private loadPatient(patientId: number): void {
    this.patientService.getPatientById(patientId).subscribe({
      next: (p) => (this.patient = p),
      error: () => {}
    });
  }

  createDrainage(): void {
    if (this.scope === 'episode' && this.episodeId) {
      this.router.navigate(['/episodes', this.episodeId, 'drainages', 'new']);
    } else if (this.patientId) {
      this.router.navigate(['/patients', this.patientId, 'drainages', 'new']);
    }
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

  get canCreate(): boolean {
    return this.authService.isUrologue();
  }

  get backLink(): unknown[] {
    if (this.scope === 'episode' && this.episodeId) {
      return ['/episodes', this.episodeId];
    }
    if (this.patientId) {
      return ['/patients', this.patientId];
    }
    return ['/'];
  }
}
