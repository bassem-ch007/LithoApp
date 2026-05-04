import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  ANALYSIS_STATUS_LABELS,
  ANALYSIS_TYPE_LABELS,
  AnalysisRequestDto,
  AnalysisStatus,
  AnalysisType
} from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';
import { EpisodeResponse } from '../../../episodes/models/episode.model';
import { EpisodeService } from '../../../episodes/services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';
import { AuthService } from '../../../../app/core/auth/auth.service';

@Component({
  selector: 'app-analysis-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './analysis-list.component.html',
  styleUrl: './analysis-list.component.scss'
})
export class AnalysisListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private analysisService = inject(AnalysisService);
  private episodeService = inject(EpisodeService);
  private patientService = inject(PatientService);
  authService = inject(AuthService);

  episodeId: number | null = null;
  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  analyses: AnalysisRequestDto[] = [];
  loading = false;
  error: string | null = null;
  statusFilter: AnalysisStatus | '' = '';

  ngOnInit(): void {
    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    if (!episodeIdParam || Number.isNaN(+episodeIdParam)) {
      this.error = "Route d'épisode invalide.";
      return;
    }
    this.episodeId = +episodeIdParam;
    this.loadEpisode();
    this.loadAnalyses();
  }

  loadAnalyses(): void {
    if (!this.episodeId) return;
    this.loading = true;
    this.error = null;
    this.analysisService.listByEpisode(this.episodeId, this.statusFilter || undefined).subscribe({
      next: (list) => {
        this.analyses = list;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 403
          ? "Vous n'avez pas l'autorisation de consulter les demandes d'analyses."
          : (err.error?.message || "Échec du chargement des demandes d'analyses.");
        this.loading = false;
      }
    });
  }

  private loadEpisode(): void {
    if (!this.episodeId) return;
    this.episodeService.getEpisodeById(this.episodeId).subscribe({
      next: (episode) => {
        this.episode = episode;
        this.loadPatient(episode.patientId);
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

  onStatusChange(value: string): void {
    this.statusFilter = (value as AnalysisStatus) || '';
    this.loadAnalyses();
  }

  createAnalysis(): void {
    if (!this.episodeId) return;
    this.router.navigate(['/episodes', this.episodeId, 'analyses', 'new']);
  }

  view(analysis: AnalysisRequestDto): void {
    this.router.navigate(['/analysis-requests', analysis.id]);
  }

  typeLabel(type: AnalysisType): string {
    return ANALYSIS_TYPE_LABELS[type];
  }

  statusLabel(status: AnalysisStatus): string {
    return ANALYSIS_STATUS_LABELS[status];
  }

  statusClass(status: AnalysisStatus): string {
    return status.toLowerCase().replace('_', '-');
  }

  get canCreate(): boolean {
    return this.authService.isUrologue();
  }
}
