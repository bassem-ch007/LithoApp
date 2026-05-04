import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CreateEpisodeRequest, EpisodeResponse, EpisodeStatus, UpdateEpisodeRequest } from '../../models/episode.model';
import { EpisodeService } from '../../services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';

@Component({
  selector: 'app-episode-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './episode-form.component.html',
  styleUrl: './episode-form.component.scss'
})
export class EpisodeFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  episodeId: number | null = null;
  patientId: number | null = null;
  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  loading = false;
  saving = false;
  error: string | null = null;
  fieldErrors: Record<string, string> = {};

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private episodeService: EpisodeService,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    this.initForm();

    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    if (episodeIdParam) {
      this.isEditMode = true;
      this.episodeId = +episodeIdParam;
      if (Number.isNaN(this.episodeId)) {
        this.error = "Route d'épisode invalide.";
        return;
      }
      this.loadEpisode();
      return;
    }

    const patientIdParam = this.route.snapshot.paramMap.get('patientId');
    if (!patientIdParam || Number.isNaN(+patientIdParam)) {
      this.error = 'Route patient invalide.';
      return;
    }

    this.patientId = +patientIdParam;
    this.loadPatient();
  }

  private initForm(): void {
    const today = new Date().toISOString().slice(0, 10);
    this.form = this.fb.group({
      openedAt: [today, Validators.required],
      title: ['', Validators.maxLength(255)],
      notes: ['', Validators.maxLength(5000)],
      recurrence: [false],
      status: ['ACTIVE' as EpisodeStatus, Validators.required]
    });
  }

  private loadEpisode(): void {
    if (!this.episodeId) return;

    this.loading = true;
    this.error = null;

    this.episodeService.getEpisodeById(this.episodeId).subscribe({
      next: (episode) => {
        this.episode = episode;
        this.patientId = episode.patientId;
        this.form.patchValue({
          openedAt: episode.openedAt,
          title: episode.title || '',
          notes: episode.notes || '',
          recurrence: episode.recurrence,
          status: episode.status
        });
        this.form.get('openedAt')?.disable();
        this.loading = false;
        this.loadPatient();
      },
      error: (err) => {
        if (err.status === 404) {
          this.error = 'Épisode introuvable.';
        } else if (err.status === 403) {
          this.error = "Vous n'avez pas l'autorisation de modifier cet épisode.";
        } else {
          this.error = err.error?.message || "Échec du chargement de l'épisode.";
        }
        this.loading = false;
      }
    });
  }

  private loadPatient(): void {
    if (!this.patientId) return;

    this.loading = true;
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 404 ? 'Patient introuvable.' : 'Échec du chargement du contexte patient.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.isEditMode) {
      this.updateEpisode();
      return;
    }

    if (!this.patientId) return;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;
    this.fieldErrors = {};

    const raw = this.form.value;
    const request: CreateEpisodeRequest = {
      patientId: this.patientId,
      openedAt: raw.openedAt,
      title: this.toNullIfEmpty(raw.title?.trim()),
      notes: this.toNullIfEmpty(raw.notes?.trim()),
      recurrence: !!raw.recurrence
    };

    this.episodeService.createEpisode(request).subscribe({
      next: (episode) => {
        this.saving = false;
        if (episode.id) {
          this.router.navigate(['/episodes', episode.id]);
        } else {
          this.router.navigate(['/patients', this.patientId, 'episodes']);
        }
      },
      error: (err) => this.handleSaveError(err)
    });
  }

  private updateEpisode(): void {
    if (!this.episodeId) return;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;
    this.fieldErrors = {};

    const raw = this.form.getRawValue();
    const request: UpdateEpisodeRequest = {
      title: this.toNullIfEmpty(raw.title?.trim()),
      notes: this.toNullIfEmpty(raw.notes?.trim()),
      recurrence: !!raw.recurrence,
      status: raw.status as EpisodeStatus
    };

    this.episodeService.updateEpisode(this.episodeId, request).subscribe({
      next: (episode) => {
        this.saving = false;
        this.router.navigate(['/episodes', episode.id]);
      },
      error: (err) => this.handleSaveError(err)
    });
  }

  cancel(): void {
    if (this.isEditMode && this.episodeId) {
      this.router.navigate(['/episodes', this.episodeId]);
    } else if (this.patientId) {
      this.router.navigate(['/patients', this.patientId, 'episodes']);
    }
  }

  hasError(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched) || !!this.fieldErrors[field];
  }

  getError(field: string): string {
    if (this.fieldErrors[field]) return this.fieldErrors[field];
    const ctrl = this.form.get(field);
    if (ctrl?.hasError('required')) return 'Ce champ est obligatoire.';
    if (ctrl?.hasError('maxlength')) {
      const max = ctrl.errors?.['maxlength']?.requiredLength;
      return `Ne doit pas dépasser ${max} caractères.`;
    }
    return '';
  }

  private handleSaveError(err: any): void {
    this.saving = false;
    const validationErrors = err.error?.fieldErrors || err.error?.errors;
    if (err.status === 400 && validationErrors) {
      this.fieldErrors = validationErrors;
      this.error = 'Veuillez corriger les erreurs de validation ci-dessous.';
    } else if (err.status === 403) {
      this.error = this.isEditMode
        ? "Vous n'avez pas l'autorisation de modifier les épisodes."
        : "Vous n'avez pas l'autorisation de créer des épisodes.";
    } else {
      this.error = err.error?.message || err.error?.detail || 'Une erreur inattendue est survenue. Veuillez réessayer.';
    }
  }

  private toNullIfEmpty(value: string | undefined): string | null {
    return value ? value : null;
  }
}
