import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EpisodeResponse } from '../../models/episode.model';
import { EpisodeService } from '../../services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';

@Component({
  selector: 'app-episode-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './episode-details.component.html',
  styleUrl: './episode-details.component.scss'
})
export class EpisodeDetailsComponent implements OnInit {
  episodeId: number | null = null;
  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  loading = false;
  patientLoading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private episodeService: EpisodeService,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    if (!episodeIdParam || Number.isNaN(+episodeIdParam)) {
      this.error = 'Invalid episode route.';
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
          this.error = 'Episode not found.';
        } else if (err.status === 403) {
          this.error = 'You do not have permission to view this episode.';
        } else {
          this.error = err.error?.message || 'Failed to load episode details.';
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
    return value ? 'Yes' : 'No';
  }
}
