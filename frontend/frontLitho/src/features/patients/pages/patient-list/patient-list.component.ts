import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { PatientSummaryResponse, Page } from '../../models/patient.model';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './patient-list.component.html',
  styleUrl: './patient-list.component.scss'
})
export class PatientListComponent implements OnInit {
  patients: PatientSummaryResponse[] = [];
  loading = false;
  error: string | null = null;

  searchTerm = '';
  searchField: 'name' | 'di' | 'dmi' | 'phone' = 'name';

  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  constructor(
    private patientService: PatientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loading = true;
    this.error = null;

    const filters: { di?: string; dmi?: string; name?: string; phone?: string } = {};
    if (this.searchTerm.trim()) {
      filters[this.searchField] = this.searchTerm.trim();
    }

    this.patientService.searchPatients(filters, this.currentPage, this.pageSize).subscribe({
      next: (page: Page<PatientSummaryResponse>) => {
        this.patients = page.content;
        this.totalElements = page.totalElements;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 403
          ? 'You do not have permission to view patients.'
          : 'Failed to load patients. Please try again.';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadPatients();
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.currentPage = 0;
    this.loadPatients();
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadPatients();
    }
  }

  viewPatient(id: number): void {
    this.router.navigate(['/patients', id]);
  }

  editPatient(id: number): void {
    this.router.navigate(['/patients', id, 'edit']);
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

  formatGender(gender: string): string {
    return gender === 'MALE' ? 'M' : 'F';
  }
}
