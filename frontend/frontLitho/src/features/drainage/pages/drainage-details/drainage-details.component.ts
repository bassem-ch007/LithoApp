import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  DRAINAGE_SIDE_LABELS,
  DRAINAGE_STATUS_LABELS,
  DRAINAGE_TYPE_LABELS,
  DrainageResponse,
  JJ_TYPE_LABELS
} from '../../models/drainage.model';
import { DrainageService } from '../../services/drainage.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';
import { AuthService } from '../../../../app/core/auth/auth.service';

@Component({
  selector: 'app-drainage-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './drainage-details.component.html',
  styleUrl: './drainage-details.component.scss'
})
export class DrainageDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private drainageService = inject(DrainageService);
  private patientService = inject(PatientService);
  authService = inject(AuthService);

  readonly typeLabels = DRAINAGE_TYPE_LABELS;
  readonly sideLabels = DRAINAGE_SIDE_LABELS;
  readonly jjLabels = JJ_TYPE_LABELS;

  drainageId: string | null = null;
  drainage: DrainageResponse | null = null;
  patient: PatientResponse | null = null;

  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  editMode = false;
  editForm!: FormGroup;
  saving = false;
  saveError: string | null = null;

  removeMode = false;
  removeForm!: FormGroup;
  removing = false;
  removeError: string | null = null;

  ngOnInit(): void {
    this.drainageId = this.route.snapshot.paramMap.get('id');
    if (!this.drainageId) {
      this.error = 'Route de drainage invalide.';
      return;
    }

    this.editForm = this.fb.group({
      plannedRemovalDate: [''],
      notes: ['']
    });
    this.removeForm = this.fb.group({
      removedAt: [new Date().toISOString().slice(0, 10), Validators.required]
    });

    this.load();
  }

  load(): void {
    if (!this.drainageId) return;
    this.loading = true;
    this.error = null;
    this.drainageService.getById(this.drainageId).subscribe({
      next: (d) => {
        this.drainage = d;
        this.editForm.patchValue({
          plannedRemovalDate: d.plannedRemovalDate ?? '',
          notes: d.notes ?? ''
        });
        this.loading = false;
        this.loadPatient(d.patientId);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 404) this.error = 'Drainage introuvable.';
        else if (err.status === 403) this.error = "Vous n'avez pas l'autorisation de consulter ce drainage.";
        else this.error = err.error?.message || 'Échec du chargement du drainage.';
      }
    });
  }

  private loadPatient(patientId: number): void {
    this.patientService.getPatientById(patientId).subscribe({
      next: (p) => (this.patient = p),
      error: () => {}
    });
  }

  // ── Edit ────────────────────────────────────────────────────────────────

  startEdit(): void {
    if (!this.drainage) return;
    this.editMode = true;
    this.removeMode = false;
    this.saveError = null;
    this.editForm.patchValue({
      plannedRemovalDate: this.drainage.plannedRemovalDate ?? '',
      notes: this.drainage.notes ?? ''
    });
  }

  cancelEdit(): void {
    this.editMode = false;
    this.saveError = null;
  }

  saveEdit(): void {
    if (!this.drainageId) return;
    this.saving = true;
    this.saveError = null;
    const v = this.editForm.value;
    this.drainageService.update(this.drainageId, {
      plannedRemovalDate: v.plannedRemovalDate ? v.plannedRemovalDate : null,
      notes: v.notes?.trim() ? v.notes.trim() : null
    }).subscribe({
      next: (d) => {
        this.saving = false;
        this.drainage = d;
        this.editMode = false;
        this.flashSuccess('Drainage mis à jour.');
      },
      error: (err) => {
        this.saving = false;
        if (err.status === 403) this.saveError = "Vous n'avez pas l'autorisation de modifier les drainages.";
        else if (err.status === 400) this.saveError = err.error?.message || 'Mise à jour invalide.';
        else this.saveError = err.error?.message || 'Échec de la mise à jour.';
      }
    });
  }

  // ── Remove ──────────────────────────────────────────────────────────────

  startRemove(): void {
    if (!this.drainage || this.drainage.status === 'REMOVED') return;
    this.removeMode = true;
    this.editMode = false;
    this.removeError = null;
    this.removeForm.patchValue({
      removedAt: new Date().toISOString().slice(0, 10)
    });
  }

  cancelRemove(): void {
    this.removeMode = false;
    this.removeError = null;
  }

  confirmRemove(): void {
    if (!this.drainageId) return;
    if (this.removeForm.invalid) {
      this.removeForm.markAllAsTouched();
      return;
    }
    this.removing = true;
    this.removeError = null;
    const v = this.removeForm.value;
    this.drainageService.remove(this.drainageId, { removedAt: v.removedAt }).subscribe({
      next: (d) => {
        this.removing = false;
        this.drainage = d;
        this.removeMode = false;
        this.flashSuccess('Drainage marqué comme retiré.');
      },
      error: (err) => {
        this.removing = false;
        if (err.status === 403) this.removeError = "Vous n'avez pas l'autorisation de retirer les drainages.";
        else if (err.status === 400) this.removeError = err.error?.message || 'Drainage déjà retiré ou date invalide.';
        else this.removeError = err.error?.message || 'Échec du retrait.';
      }
    });
  }

  private flashSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => (this.successMessage = null), 3000);
  }

  // ── View helpers ────────────────────────────────────────────────────────

  statusClass(): string {
    if (!this.drainage) return '';
    if (this.drainage.status === 'REMOVED') return 'removed';
    if (this.drainage.overdue) return 'overdue';
    return 'active';
  }

  statusLabel(): string {
    if (!this.drainage) return '';
    if (this.drainage.status === 'ACTIVE' && this.drainage.overdue) return DRAINAGE_STATUS_LABELS.OVERDUE;
    return DRAINAGE_STATUS_LABELS[this.drainage.status];
  }

  get canEdit(): boolean {
    return !!this.drainage && this.authService.isUrologue() && this.drainage.status === 'ACTIVE';
  }

  get canRemove(): boolean {
    return !!this.drainage && this.authService.isUrologue() && this.drainage.status === 'ACTIVE';
  }

  get backLink(): unknown[] {
    if (this.drainage) {
      return ['/episodes', this.drainage.episodeId, 'drainages'];
    }
    return ['/'];
  }
}
