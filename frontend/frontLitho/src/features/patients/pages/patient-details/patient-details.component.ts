import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { PatientResponse } from '../../models/patient.model';

@Component({
  selector: 'app-patient-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './patient-details.component.html',
  styleUrl: './patient-details.component.scss'
})
export class PatientDetailsComponent implements OnInit {
  patient: PatientResponse | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPatient(+id);
    }
  }

  private loadPatient(id: number): void {
    this.loading = true;
    this.error = null;

    this.patientService.getPatientById(id).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.loading = false;
      },
      error: (err) => {
        if (err.status === 404) {
          this.error = 'Patient introuvable.';
        } else if (err.status === 403) {
          this.error = "Vous n'avez pas l'autorisation de consulter ce patient.";
        } else {
          this.error = 'Échec du chargement des détails du patient.';
        }
        this.loading = false;
      }
    });
  }

  editPatient(): void {
    if (this.patient) {
      this.router.navigate(['/patients', this.patient.id, 'edit']);
    }
  }

  createEpisode(): void {
    if (this.patient) {
      this.router.navigate(['/patients', this.patient.id, 'episodes', 'new']);
    }
  }

  viewEpisodes(): void {
    if (this.patient) {
      this.router.navigate(['/patients', this.patient.id, 'episodes']);
    }
  }

  viewDrainages(): void {
    if (this.patient) {
      this.router.navigate(['/patients', this.patient.id, 'drainages']);
    }
  }

  goToAnalysesViaEpisodes(): void {
    if (this.patient) {
      this.router.navigate(['/patients', this.patient.id, 'episodes']);
    }
  }

  formatGender(gender: string): string {
    return gender === 'MALE' ? 'Homme' : 'Femme';
  }

  formatBoolean(val: boolean | null): string {
    if (val === null || val === undefined) return '-';
    return val ? 'Oui' : 'Non';
  }

  formatKidneyType(value: string | null | undefined): string {
    if (!value) return '-';
    if (value === 'ANATOMICAL') return 'Anatomique';
    if (value === 'FUNCTIONAL') return 'Fonctionnel';
    return value;
  }
}
