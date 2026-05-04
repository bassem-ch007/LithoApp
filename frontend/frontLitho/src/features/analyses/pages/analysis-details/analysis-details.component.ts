import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  ANALYSIS_STATUS_LABELS,
  ANALYSIS_TYPE_LABELS,
  AnalysisRequestDto,
  AnalysisStatus,
  AnalysisType,
  AuditActionType,
  AuditEntryDto,
  MetabolicDocumentType,
  METABOLIC_DOCUMENT_LABELS,
  MetabolicResultDto,
  StoneResultDto
} from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';
import { AuthService } from '../../../../app/core/auth/auth.service';
import { MetabolicPanelComponent } from '../../components/metabolic-panel/metabolic-panel.component';
import { StonePanelComponent } from '../../components/stone-panel/stone-panel.component';

@Component({
  selector: 'app-analysis-details',
  standalone: true,
  imports: [CommonModule, RouterLink, MetabolicPanelComponent, StonePanelComponent],
  templateUrl: './analysis-details.component.html',
  styleUrl: './analysis-details.component.scss'
})
export class AnalysisDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private analysisService = inject(AnalysisService);
  private patientService = inject(PatientService);
  authService = inject(AuthService);

  analysisId: number | null = null;
  analysis: AnalysisRequestDto | null = null;
  patient: PatientResponse | null = null;
  metabolicResult: MetabolicResultDto | null = null;
  stoneResult: StoneResultDto | null = null;
  auditLog: AuditEntryDto[] = [];

  loading = false;
  error: string | null = null;
  completing = false;
  completeError: string | null = null;
  successMessage: string | null = null;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam || Number.isNaN(+idParam)) {
      this.error = "Route d'analyse invalide.";
      return;
    }
    this.analysisId = +idParam;
    this.loadAll();
  }

  loadAll(): void {
    if (!this.analysisId) return;
    this.loading = true;
    this.error = null;

    this.analysisService.getById(this.analysisId).subscribe({
      next: (a) => {
        this.analysis = a;
        this.loadDetails(a);
        this.loadPatient(a.patientId);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 404) {
          this.error = "Demande d'analyse introuvable.";
        } else if (err.status === 403) {
          this.error = "Vous n'avez pas l'autorisation de consulter cette demande d'analyse.";
        } else {
          this.error = err.error?.message || "Échec du chargement de la demande d'analyse.";
        }
      }
    });
  }

  private loadDetails(analysis: AnalysisRequestDto): void {
    if (!this.analysisId) return;

    const metabolic$ = analysis.type === 'METABOLIC'
      ? this.analysisService.getMetabolicResult(this.analysisId).pipe(catchError(() => of(null)))
      : of(null);

    const stone$ = analysis.type === 'STONE'
      ? this.analysisService.getStoneResult(this.analysisId).pipe(catchError(() => of(null)))
      : of(null);

    const audit$ = this.analysisService.getAuditLog(this.analysisId).pipe(catchError(() => of([] as AuditEntryDto[])));

    forkJoin({ metabolic: metabolic$, stone: stone$, audit: audit$ }).subscribe({
      next: (res) => {
        this.metabolicResult = res.metabolic;
        this.stoneResult = res.stone;
        this.auditLog = res.audit;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private loadPatient(patientId: number): void {
    this.patientService.getPatientById(patientId).subscribe({
      next: (p) => (this.patient = p),
      error: () => {}
    });
  }

  refreshAfterChange(): void {
    if (!this.analysisId) return;
    this.analysisService.getById(this.analysisId).subscribe({
      next: (a) => {
        this.analysis = a;
        this.loadDetails(a);
      }
    });
  }

  complete(): void {
    if (!this.analysisId || !this.analysis) return;
    if (this.analysis.status !== 'IN_PROGRESS') return;

    this.completing = true;
    this.completeError = null;

    this.analysisService.complete(this.analysisId).subscribe({
      next: (a) => {
        this.completing = false;
        this.analysis = a;
        this.successMessage = "Demande d'analyse marquée comme terminée.";
        this.refreshAfterChange();
        setTimeout(() => (this.successMessage = null), 3000);
      },
      error: (err) => {
        this.completing = false;
        if (err.status === 403) {
          this.completeError = "Seuls les biologistes peuvent terminer les demandes d'analyses.";
        } else if (err.status === 400) {
          this.completeError = err.error?.message || "Impossible de terminer : données requises manquantes.";
        } else if (err.status === 404) {
          this.completeError = "Demande d'analyse introuvable.";
        } else {
          this.completeError = err.error?.message || 'Échec de la finalisation de la demande.';
        }
      }
    });
  }

  // ── Permissions ────────────────────────────────────────────────────────

  get isCompleted(): boolean {
    return this.analysis?.status === 'COMPLETED';
  }

  get canEditMetabolic(): boolean {
    return !!this.analysis
      && this.authService.isBiologist()
      && this.analysis.type === 'METABOLIC'
      && this.analysis.status !== 'COMPLETED';
  }

  get canEditStone(): boolean {
    return !!this.analysis
      && this.authService.isBiologist()
      && this.analysis.type === 'STONE'
      && this.analysis.status !== 'COMPLETED';
  }

  get canComplete(): boolean {
    return !!this.analysis
      && this.authService.isBiologist()
      && this.analysis.status === 'IN_PROGRESS';
  }

  get backLink(): unknown[] {
    if (this.authService.isBiologist()) {
      return ['/analysis-search'];
    }
    if (this.analysis) {
      return ['/episodes', this.analysis.episodeId, 'analyses'];
    }
    return ['/'];
  }

  statusClass(status: AnalysisStatus): string {
    return status.toLowerCase().replace('_', '-');
  }

  statusLabel(status: AnalysisStatus): string {
    return ANALYSIS_STATUS_LABELS[status];
  }

  typeLabel(type: AnalysisType): string {
    return ANALYSIS_TYPE_LABELS[type];
  }

  auditActionLabel(action: AuditActionType): string {
    const labels: Record<AuditActionType, string> = {
      REQUEST_CREATED: 'Demande créée',
      STATUS_CHANGED: 'Statut modifié',
      PDF_UPLOADED: 'PDF téléversé',
      PDF_REPLACED: 'PDF remplacé',
      STONE_RESULT_CREATED: 'Résultat du calcul créé',
      STONE_RESULT_FIELD_UPDATED: 'Résultat du calcul modifié',
      REQUEST_COMPLETED: 'Demande terminée'
    };
    return labels[action] ?? action;
  }

  auditValueLabel(value: string | null): string {
    if (!value) return '';
    if (value in ANALYSIS_STATUS_LABELS) {
      return ANALYSIS_STATUS_LABELS[value as AnalysisStatus];
    }
    if (value in ANALYSIS_TYPE_LABELS) {
      return ANALYSIS_TYPE_LABELS[value as AnalysisType];
    }
    if (value in METABOLIC_DOCUMENT_LABELS) {
      return METABOLIC_DOCUMENT_LABELS[value as MetabolicDocumentType];
    }
    return value;
  }
}
