import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ANALYSIS_STATUS_LABELS, ANALYSIS_TYPE_LABELS, AnalysisRequestDto, AnalysisStatus } from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';

@Component({
  selector: 'app-analysis-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './analysis-search.component.html',
  styleUrl: './analysis-search.component.scss'
})
export class AnalysisSearchComponent implements OnInit {
  private fb = inject(FormBuilder);
  private analysisService = inject(AnalysisService);
  private router = inject(Router);

  form: FormGroup = this.fb.group({
    di: [''],
    dmi: [''],
    name: [''],
    phone: [''],
    status: ['']
  });

  results: AnalysisRequestDto[] = [];
  loading = false;
  searched = false;
  showingDefaultQueue = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadDefaultQueue();
  }

  loadDefaultQueue(): void {
    this.loading = true;
    this.error = null;
    this.searched = false;
    this.showingDefaultQueue = true;

    forkJoin({
      created: this.analysisService.listByStatus('CREATED'),
      inProgress: this.analysisService.listByStatus('IN_PROGRESS')
    }).subscribe({
      next: ({ created, inProgress }) => {
        const merged = [...created, ...inProgress];
        merged.sort((a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.results = merged;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.results = [];
        if (err.status === 403) {
          this.error = "Vous n'avez pas l'autorisation de consulter les demandes d'analyses.";
        } else {
          this.error = err.error?.message || 'Échec du chargement de la file de traitement.';
        }
      }
    });
  }

  search(): void {
    const v = this.form.value;
    const di = (v.di || '').trim();
    const dmi = (v.dmi || '').trim();
    const name = (v.name || '').trim();
    const phone = (v.phone || '').trim();

    if (!di && !dmi && !name && !phone) {
      this.error = 'Renseignez au moins un critère (DI, DMI, nom ou téléphone).';
      return;
    }

    this.loading = true;
    this.error = null;
    this.searched = true;
    this.showingDefaultQueue = false;

    this.analysisService
      .search({
        di: di || undefined,
        dmi: dmi || undefined,
        name: name || undefined,
        phone: phone || undefined,
        status: (v.status as AnalysisStatus) || undefined
      })
      .subscribe({
        next: (list) => {
          this.results = list;
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;
          this.results = [];
          if (err.status === 400) {
            this.error = "Aucun critère d'identité fourni.";
          } else if (err.status === 403) {
            this.error = "Vous n'avez pas l'autorisation de rechercher des demandes d'analyses.";
          } else {
            this.error = err.error?.message || 'La recherche a échoué.';
          }
        }
      });
  }

  reset(): void {
    this.form.reset({ di: '', dmi: '', name: '', phone: '', status: '' });
    this.error = null;
    this.loadDefaultQueue();
  }

  open(a: AnalysisRequestDto): void {
    this.router.navigate(['/analysis-requests', a.id]);
  }

  statusClass(status: AnalysisStatus): string {
    return status.toLowerCase().replace('_', '-');
  }

  statusLabel(status: AnalysisStatus): string {
    switch (status) {
      case 'CREATED': return ANALYSIS_STATUS_LABELS.CREATED;
      case 'IN_PROGRESS': return ANALYSIS_STATUS_LABELS.IN_PROGRESS;
      case 'COMPLETED': return ANALYSIS_STATUS_LABELS.COMPLETED;
    }
  }

  typeLabel(type: 'METABOLIC' | 'STONE'): string {
    return ANALYSIS_TYPE_LABELS[type];
  }
}
