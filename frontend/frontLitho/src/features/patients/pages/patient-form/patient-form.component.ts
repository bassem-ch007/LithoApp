import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { PatientResponse, CreatePatientRequest, UpdatePatientRequest, Gender, KidneyType } from '../../models/patient.model';

@Component({
  selector: 'app-patient-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './patient-form.component.html',
  styleUrl: './patient-form.component.scss'
})
export class PatientFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  patientId: number | null = null;
  loading = false;
  saving = false;
  error: string | null = null;
  fieldErrors: Record<string, string> = {};

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    this.initForm();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.patientId = +idParam;
      this.loadPatient();
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      di: ['', Validators.required],
      dmi: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: ['', Validators.required],
      gender: ['', Validators.required],
      height: [null],
      weight: [null],
      address: [''],
      email: ['', Validators.email],
      phone: [''],
      // Clinical info
      familyHistory: [null],
      personalHistory: [null],
      lastEpisodeDate: [null],
      lithiasisType: [''],
      frequentInfections: [null],
      singleKidney: [null],
      kidneyType: [null],
      chronicRenalFailure: [null],
      clearance: [null],
      // Medication
      hasMedication: [false],
      medicationDescription: [''],
      // Lists
      associatedDiseases: this.fb.array([]),
      geneticDiseases: this.fb.array([]),
      anatomicalAnomalies: this.fb.array([])
    });
  }

  private loadPatient(): void {
    if (!this.patientId) return;
    this.loading = true;
    this.error = null;

    this.patientService.getPatientById(this.patientId).subscribe({
      next: (patient: PatientResponse) => {
        this.patchForm(patient);
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 404 ? 'Patient not found.' : 'Failed to load patient.';
        this.loading = false;
      }
    });
  }

  private patchForm(p: PatientResponse): void {
    this.form.patchValue({
      di: p.di,
      dmi: p.dmi,
      firstName: p.firstName,
      lastName: p.lastName,
      birthDate: p.birthDate,
      gender: p.gender,
      height: p.height,
      weight: p.weight,
      address: p.address || '',
      email: p.email || '',
      phone: p.phone || '',
      familyHistory: p.clinicalInfo?.familyHistory ?? null,
      personalHistory: p.clinicalInfo?.personalHistory ?? null,
      lastEpisodeDate: p.clinicalInfo?.lastEpisodeDate ?? null,
      lithiasisType: p.clinicalInfo?.lithiasisType || '',
      frequentInfections: p.clinicalInfo?.frequentInfections ?? null,
      singleKidney: p.clinicalInfo?.singleKidney ?? null,
      kidneyType: p.clinicalInfo?.kidneyType ?? null,
      chronicRenalFailure: p.clinicalInfo?.chronicRenalFailure ?? null,
      clearance: p.clinicalInfo?.clearance ?? null,
      hasMedication: p.medication?.hasMedication ?? false,
      medicationDescription: p.medication?.description || ''
    });

    // DI and DMI are not editable on update
    if (this.isEditMode) {
      this.form.get('di')!.disable();
      this.form.get('dmi')!.disable();
    }

    this.setListArray('associatedDiseases', p.associatedDiseases || []);
    this.setListArray('geneticDiseases', p.geneticDiseases || []);
    this.setListArray('anatomicalAnomalies', p.anatomicalAnomalies || []);
  }

  get associatedDiseases(): FormArray {
    return this.form.get('associatedDiseases') as FormArray;
  }

  get geneticDiseases(): FormArray {
    return this.form.get('geneticDiseases') as FormArray;
  }

  get anatomicalAnomalies(): FormArray {
    return this.form.get('anatomicalAnomalies') as FormArray;
  }

  addListItem(arrayName: string): void {
    (this.form.get(arrayName) as FormArray).push(this.fb.control('', Validators.required));
  }

  removeListItem(arrayName: string, index: number): void {
    (this.form.get(arrayName) as FormArray).removeAt(index);
  }

  private setListArray(arrayName: string, values: string[]): void {
    const arr = this.form.get(arrayName) as FormArray;
    arr.clear();
    values.forEach(v => arr.push(this.fb.control(v, Validators.required)));
  }

  private getListValues(arrayName: string): string[] {
    return (this.form.get(arrayName) as FormArray).controls
      .map(c => c.value?.trim())
      .filter((v: string) => v);
  }

  private toNullIfEmpty(val: any): any {
    if (val === '' || val === undefined) return null;
    return val;
  }

  private buildClinicalInfo(): any {
    const ci = {
      familyHistory: this.form.value.familyHistory,
      personalHistory: this.form.value.personalHistory,
      lastEpisodeDate: this.toNullIfEmpty(this.form.value.lastEpisodeDate),
      lithiasisType: this.toNullIfEmpty(this.form.value.lithiasisType),
      frequentInfections: this.form.value.frequentInfections,
      singleKidney: this.form.value.singleKidney,
      kidneyType: this.toNullIfEmpty(this.form.value.kidneyType),
      chronicRenalFailure: this.form.value.chronicRenalFailure,
      clearance: this.form.value.clearance
    };
    const hasValue = Object.values(ci).some(v => v !== null && v !== undefined);
    return hasValue ? ci : null;
  }

  private buildMedication(): any {
    const hasMed = this.form.value.hasMedication;
    if (hasMed === null || hasMed === undefined || hasMed === false) {
      if (this.form.value.medicationDescription?.trim()) {
        return { hasMedication: false, description: this.form.value.medicationDescription.trim() };
      }
      return null;
    }
    return {
      hasMedication: hasMed,
      description: this.toNullIfEmpty(this.form.value.medicationDescription?.trim())
    };
  }

  onSubmit(): void {


    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;
    this.fieldErrors = {};

    if (this.isEditMode && this.patientId) {
      const req: UpdatePatientRequest = {
        firstName: this.form.value.firstName,
        lastName: this.form.value.lastName,
        birthDate: this.form.value.birthDate,
        gender: this.form.value.gender as Gender,
        height: this.form.value.height || null,
        weight: this.form.value.weight || null,
        address: this.toNullIfEmpty(this.form.value.address),
        email: this.toNullIfEmpty(this.form.value.email),
        phone: this.toNullIfEmpty(this.form.value.phone),
        clinicalInfo: this.buildClinicalInfo(),
        medication: this.buildMedication(),
        associatedDiseases: this.getListValues('associatedDiseases'),
        geneticDiseases: this.getListValues('geneticDiseases'),
        anatomicalAnomalies: this.getListValues('anatomicalAnomalies')
      };

      this.patientService.updatePatient(this.patientId, req).subscribe({
        next: (resp) => {
          this.saving = false;
          this.router.navigate(['/patients', resp.id]);
        },
        error: (err) => this.handleSaveError(err)
      });
    } else {
      const raw = this.form.getRawValue();
      const req: CreatePatientRequest = {
        di: raw.di,
        dmi: raw.dmi,
        firstName: raw.firstName,
        lastName: raw.lastName,
        birthDate: raw.birthDate,
        gender: raw.gender as Gender,
        height: raw.height || null,
        weight: raw.weight || null,
        address: this.toNullIfEmpty(raw.address),
        email: this.toNullIfEmpty(raw.email),
        phone: this.toNullIfEmpty(raw.phone),
        clinicalInfo: this.buildClinicalInfo(),
        medication: this.buildMedication(),
        associatedDiseases: this.getListValues('associatedDiseases'),
        geneticDiseases: this.getListValues('geneticDiseases'),
        anatomicalAnomalies: this.getListValues('anatomicalAnomalies')
      };

      this.patientService.createPatient(req).subscribe({
        next: (resp) => {
          this.saving = false;
          this.router.navigate(['/patients', resp.id]);
        },
        error: (err) => this.handleSaveError(err)
      });
    }
  }

  private handleSaveError(err: any): void {
    this.saving = false;
    if (err.status === 400 && err.error?.errors) {
      this.fieldErrors = err.error.errors;
      this.error = 'Please fix the validation errors below.';
    } else if (err.status === 409) {
      this.error = err.error?.detail || 'A patient with this DI or DMI already exists.';
    } else if (err.status === 403) {
      this.error = 'You do not have permission to perform this action.';
    } else {
      this.error = 'An unexpected error occurred. Please try again.';
    }
  }

  hasError(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched) || !!this.fieldErrors[field];
  }

  getError(field: string): string {
    if (this.fieldErrors[field]) return this.fieldErrors[field];
    const ctrl = this.form.get(field);
    if (ctrl?.hasError('required')) return 'This field is required.';
    if (ctrl?.hasError('email')) return 'Invalid email address.';
    return '';
  }
}
