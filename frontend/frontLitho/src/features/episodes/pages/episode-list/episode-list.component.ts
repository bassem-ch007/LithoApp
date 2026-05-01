import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EpisodeSummaryResponse } from '../../models/episode.model';
import { EpisodeService } from '../../services/episode.service';
import { PatientResponse } from '../../../patients/models/patient.model';
import { PatientService } from '../../../patients/services/patient.service';

@Component({
  selector: 'app-episode-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './episode-list.component.html',
  styleUrl: './episode-list.component.scss'
})
export class EpisodeListComponent implements OnInit {
  patientId: number | null = null;
  patient: PatientResponse | null = null;
  episodes: EpisodeSummaryResponse[] = [];
  loading = false;
  patientLoading = false;
  error: string | null = null;

  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private episodeService: EpisodeService,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    const patientIdParam = this.route.snapshot.paramMap.get('patientId');
    if (!patientIdParam || Number.isNaN(+patientIdParam)) {
      this.error = 'Invalid patient route.';
      return;
    }

    this.patientId = +patientIdParam;
    this.loadPatient();
    this.loadEpisodes();
  }

  loadEpisodes(): void {
    if (!this.patientId) return;

    this.loading = true;
    this.error = null;

    this.episodeService.getEpisodesByPatient(this.patientId, this.currentPage, this.pageSize).subscribe({
      next: (page) => {
        this.episodes = page.content;
        this.totalElements = page.totalElements;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 403
          ? 'You do not have permission to view episodes.'
          : this.extractErrorMessage(err, 'Failed to load episodes.');
        this.loading = false;
      }
    });
  }

  private loadPatient(): void {
    if (!this.patientId) return;

    this.patientLoading = true;
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.patientLoading = false;
      },
      error: () => {
        this.patientLoading = false;
      }
    });
  }

  createEpisode(): void {
    if (this.patientId) {
      this.router.navigate(['/patients', this.patientId, 'episodes', 'new']);
    }
  }

  viewEpisode(episodeId: number): void {
    this.router.navigate(['/episodes', episodeId]);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadEpisodes();
    }
  }

  get pageNumbers(): number[] {
    const pages: number[] = [];
    const start = Math.max(0, this.currentPage - 2);
    const end = Math.min(this.totalPages, start + 5);
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }

  private extractErrorMessage(err: any, fallback: string): string {
    return err.error?.message || err.error?.detail || fallback;
  }
}
