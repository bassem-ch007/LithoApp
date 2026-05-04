import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  CreateDrainageRequest,
  DRAINAGE_SIDES,
  DRAINAGE_SIDE_LABELS,
  DRAINAGE_TYPES,
  DRAINAGE_TYPE_LABELS,
  DrainageSide,
  DrainageType,
  JJType,
  JJ_TYPES,
  JJ_TYPE_LABELS
} from '../../models/drainage.model';
import { DrainageService } from '../../services/drainage.service';
import { EpisodeResponse } from '../../../episodes/models/episode.model';
import { EpisodeService } from '../../../episodes/services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';

@Component({
  selector: 'app-drainage-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './drainage-create.component.html',
  styleUrl: './drainage-create.component.scss'
})
export class DrainageCreateComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private drainageService = inject(DrainageService);
  private episodeService = inject(EpisodeService);
  private patientService = inject(PatientService);

  readonly drainageTypes = DRAINAGE_TYPES;
  readonly drainageSides = DRAINAGE_SIDES;
  readonly jjTypes = JJ_TYPES;
  readonly typeLabels = DRAINAGE_TYPE_LABELS;
  readonly sideLabels = DRAINAGE_SIDE_LABELS;
  readonly jjLabels = JJ_TYPE_LABELS;

  form!: FormGroup;
  episodeId: number | null = null;
  episode: EpisodeResponse | null = null;
  patient: PatientResponse | null = null;
  loading = false;
  saving = false;
  error: string | null = null;
  fieldErrors: Record<string, string> = {};

  ngOnInit(): void {
    const episodeIdParam = this.route.snapshot.paramMap.get('episodeId');
    if (!episodeIdParam || Number.isNaN(+episodeIdParam)) {
      this.error = "Route d'épisode invalide - les drainages doivent être créés depuis un épisode.";
      return;
    }
    this.episodeId = +episodeIdParam;

    const today = new Date().toISOString().slice(0, 10);
    this.form = this.fb.group({
      drainageType: ['JJ' as DrainageType, Validators.required],
      side: ['LEFT' as DrainageSide, Validators.required],
      placedAt: [today, Validators.required],
      plannedRemovalDate: [''],
      jjType: ['STANDARD_6F' as JJType],
      notes: ['']
    });

    this.form.get('drainageType')?.valueChanges.subscribe((type: DrainageType) => {
      const jjCtrl = this.form.get('jjType');
      if (type === 'JJ') {
        jjCtrl?.setValidators([Validators.required]);
        if (!jjCtrl?.value) {
          jjCtrl?.setValue('STANDARD_6F');
        }
      } else {
        jjCtrl?.clearValidators();
        jjCtrl?.setValue(null);
      }
      jjCtrl?.updateValueAndValidity();
    });

    this.loadEpisode();
  }

  private loadEpisode(): void {
    if (!this.episodeId) return;
    this.loading = true;
    this.episodeService.getEpisodeById(this.episodeId).subscribe({
      next: (e) => {
        this.episode = e;
        this.loadPatient(e.patientId);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.status === 404 ? 'Épisode introuvable.' : (err.error?.message || "Échec du chargement de l'épisode.");
      }
    });
  }

  private loadPatient(patientId: number): void {
    this.patientService.getPatientById(patientId).subscribe({
      next: (p) => {
        this.patient = p;
        this.loading = false;
      },
      error: () => (this.loading = false)
    });
  }

  onSubmit(): void {
    if (!this.episodeId || !this.episode) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;
    this.fieldErrors = {};

    const v = this.form.value;
    const request: CreateDrainageRequest = {
      episodeId: this.episodeId,
      patientId: this.episode.patientId,
      drainageType: v.drainageType,
      side: v.side,
      placedAt: v.placedAt,
      plannedRemovalDate: this.toNullIfEmpty(v.plannedRemovalDate),
      jjType: v.drainageType === 'JJ' ? (v.jjType ?? null) : null,
      notes: this.toNullIfEmpty(v.notes?.trim())
    };

    this.drainageService.create(request).subscribe({
      next: (created) => {
        this.saving = false;
        this.router.navigate(['/drainages', created.id]);
      },
      error: (err) => this.handleError(err)
    });
  }

  private handleError(err: any): void {
    this.saving = false;
    const validationErrors = err.error?.fieldErrors || err.error?.errors;
    if (err.status === 400 && validationErrors) {
      this.fieldErrors = validationErrors;
      this.error = 'Veuillez corriger les erreurs de validation ci-dessous.';
    } else if (err.status === 400) {
      this.error = err.error?.message || 'Demande invalide.';
    } else if (err.status === 403) {
      this.error = "Vous n'avez pas l'autorisation de créer des drainages.";
    } else if (err.status === 404) {
      this.error = 'Patient ou épisode introuvable.';
    } else if (err.status === 409) {
      this.error = err.error?.message || 'Un drainage actif identique existe déjà.';
    } else {
      this.error = err.error?.message || 'Échec de la création du drainage.';
    }
  }

  cancel(): void {
    if (this.episodeId) {
      this.router.navigate(['/episodes', this.episodeId, 'drainages']);
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
    return '';
  }

  get isJJ(): boolean {
    return this.form?.value?.drainageType === 'JJ';
  }

  private toNullIfEmpty(value: string | null | undefined): string | null {
    return value ? value : null;
  }
}
