import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EpisodeResponse, EpisodeStatus } from '../../models/episode.model';
import { EpisodeService } from '../../services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';
import { AuthService } from '../../../../app/core/auth/auth.service';

@Component({
  selector: 'app-episode-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './episode-details.component.html',
  styleUrl: './episode-details.component.scss'
})
export class EpisodeDetailsComponent implements OnInit {
  authService = inject(AuthService);

  episodeId: number | null = null;
  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  loading = false;
  patientLoading = false;
  error: string | null = null;
  closing = false;
  closeError: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private episodeService: EpisodeService,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    if (!episodeIdParam || Number.isNaN(+episodeIdParam)) {
      this.error = 'Route d\'épisode invalide.';
      return;
    }

    this.episodeId = +episodeIdParam;
    this.loadEpisode();
  }

  loadEpisode(): void {
    if (!this.episodeId) return;

    this.loading = true;
    this.error = null;

    this.episodeService.getEpisodeById(this.episodeId).subscribe({
      next: (episode) => {
        this.episode = episode;
        this.loading = false;
        this.loadPatient(episode.patientId);
      },
      error: (err) => {
        if (err.status === 404) {
          this.error = 'Épisode introuvable.';
        } else if (err.status === 403) {
          this.error = "Vous n'avez pas l'autorisation de consulter cet épisode.";
        } else {
          this.error = err.error?.message || "Échec du chargement de l'épisode.";
        }
        this.loading = false;
      }
    });
  }

  private loadPatient(patientId: number): void {
    this.patientLoading = true;
    this.patientService.getPatientById(patientId).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.patientLoading = false;
      },
      error: () => {
        this.patientLoading = false;
      }
    });
  }

  formatBoolean(value: boolean): string {
    return value ? 'Oui' : 'Non';
  }

  statusLabel(status: EpisodeStatus): string {
    return status === 'CLOSED' ? 'Clôturé' : 'Ouvert';
  }

  get canClose(): boolean {
    return this.authService.isUrologue()
      && !!this.episode
      && this.episode.status === 'ACTIVE';
  }

  closeEpisode(): void {
    if (!this.episode || !this.episodeId) return;
    if (!confirm("Confirmer la clôture de cet épisode ? Cette action change le statut en 'Clôturé'.")) return;

    this.closing = true;
    this.closeError = null;
    this.episodeService.updateEpisode(this.episodeId, { status: 'CLOSED' }).subscribe({
      next: (e) => {
        this.episode = e;
        this.closing = false;
      },
      error: (err) => {
        this.closing = false;
        if (err.status === 403) {
          this.closeError = "Vous n'avez pas l'autorisation de clôturer cet épisode.";
        } else if (err.status === 400) {
          this.closeError = err.error?.message || "Transition de statut invalide.";
        } else {
          this.closeError = err.error?.message || "Échec de la clôture de l'épisode.";
        }
      }
    });
  }
}
