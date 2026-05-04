import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AnalysisType, CreateAnalysisRequestDto } from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';
import { EpisodeResponse } from '../../../episodes/models/episode.model';
import { EpisodeService } from '../../../episodes/services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';

@Component({
  selector: 'app-analysis-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './analysis-create.component.html',
  styleUrl: './analysis-create.component.scss'
})
export class AnalysisCreateComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private analysisService = inject(AnalysisService);
  private episodeService = inject(EpisodeService);
  private patientService = inject(PatientService);

  form!: FormGroup;
  episodeId: number | null = null;
  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  loading = false;
  saving = false;
  error: string | null = null;

  ngOnInit(): void {
    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    if (!episodeIdParam || Number.isNaN(+episodeIdParam)) {
      this.error = "Route d'épisode invalide.";
      return;
    }
    this.episodeId = +episodeIdParam;

    this.form = this.fb.group({
      type: ['METABOLIC' as AnalysisType, Validators.required]
    });

    this.loadEpisode();
  }

  private loadEpisode(): void {
    if (!this.episodeId) return;
    this.loading = true;
    this.episodeService.getEpisodeById(this.episodeId).subscribe({
      next: (episode) => {
        this.episode = episode;
        this.loadPatient(episode.patientId);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.status === 404
          ? 'Épisode introuvable.'
          : (err.error?.message || "Échec du chargement de l'épisode.");
      }
    });
  }

  private loadPatient(patientId: number): void {
    this.patientService.getPatientById(patientId).subscribe({
      next: (p) => {
        this.patient = p;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  selectType(type: AnalysisType): void {
    this.form.patchValue({ type });
  }

  onSubmit(): void {
    if (!this.episodeId || !this.episode) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;

    const request: CreateAnalysisRequestDto = {
      patientId: this.episode.patientId,
      episodeId: this.episodeId,
      type: this.form.value.type as AnalysisType
    };

    this.analysisService.createRequest(request).subscribe({
      next: (created) => {
        this.saving = false;
        this.router.navigate(['/analysis-requests', created.id]);
      },
      error: (err) => {
        this.saving = false;
        if (err.status === 403) {
          this.error = "Vous n'avez pas l'autorisation de créer des demandes d'analyses.";
        } else if (err.status === 400) {
          this.error = err.error?.message || 'Demande invalide.';
        } else if (err.status === 404) {
          this.error = 'Patient ou épisode introuvable.';
        } else {
          this.error = err.error?.message || "Échec de la création de la demande d'analyse.";
        }
      }
    });
  }

  cancel(): void {
    if (this.episodeId) {
      this.router.navigate(['/episodes', this.episodeId, 'analyses']);
    }
  }
}
