import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AnalysisRequestDto, StoneResultDto, UpdateStoneResultDto } from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';

interface FieldDef {
  key: keyof UpdateStoneResultDto;
  label: string;
  placeholder?: string;
}

const MORPH_FIELDS: FieldDef[] = [
  { key: 'morphSize', label: 'Taille', placeholder: 'ex. 6 mm' },
  { key: 'morphSurface', label: 'Surface' },
  { key: 'morphColor', label: 'Couleur' },
  { key: 'morphSection', label: 'Section' },
  { key: 'morphOuterLayers', label: 'Couches externes' },
  { key: 'morphCore', label: 'Noyau' }
];

const SPECTRO_FIELDS: FieldDef[] = [
  { key: 'spectroSurface', label: 'Surface' },
  { key: 'spectroSection', label: 'Section' },
  { key: 'spectroOuterLayers', label: 'Couches externes' },
  { key: 'spectroCore', label: 'Noyau' }
];

@Component({
  selector: 'app-stone-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './stone-panel.component.html',
  styleUrl: './stone-panel.component.scss'
})
export class StonePanelComponent implements OnChanges {
  @Input({ required: true }) analysis!: AnalysisRequestDto;
  @Input() result: StoneResultDto | null = null;
  @Input() canEdit = false;
  @Output() resultChanged = new EventEmitter<void>();

  readonly morphFields = MORPH_FIELDS;
  readonly spectroFields = SPECTRO_FIELDS;

  private fb = inject(FormBuilder);
  private analysisService = inject(AnalysisService);

  form: FormGroup = this.fb.group({
    morphSize: [''],
    morphSurface: [''],
    morphColor: [''],
    morphSection: [''],
    morphOuterLayers: [''],
    morphCore: [''],
    spectroSurface: [''],
    spectroSection: [''],
    spectroOuterLayers: [''],
    spectroCore: [''],
    finalStoneType: ['']
  });

  saving = false;
  saveError: string | null = null;
  saveSuccess: string | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['result'] || changes['analysis']) {
      this.applyResultToForm();
      this.applyEditableState();
    }
    if (changes['canEdit']) {
      this.applyEditableState();
    }
  }

  private applyResultToForm(): void {
    const r = this.result;
    this.form.reset({
      morphSize: r?.morphSize ?? '',
      morphSurface: r?.morphSurface ?? '',
      morphColor: r?.morphColor ?? '',
      morphSection: r?.morphSection ?? '',
      morphOuterLayers: r?.morphOuterLayers ?? '',
      morphCore: r?.morphCore ?? '',
      spectroSurface: r?.spectroSurface ?? '',
      spectroSection: r?.spectroSection ?? '',
      spectroOuterLayers: r?.spectroOuterLayers ?? '',
      spectroCore: r?.spectroCore ?? '',
      finalStoneType: r?.finalStoneType ?? ''
    });
  }

  private applyEditableState(): void {
    if (this.canEdit) {
      this.form.enable({ emitEvent: false });
    } else {
      this.form.disable({ emitEvent: false });
    }
  }

  save(): void {
    if (!this.canEdit) return;
    this.saving = true;
    this.saveError = null;
    this.saveSuccess = null;

    const dto = this.buildPatchDto();

    this.analysisService.updateStoneResult(this.analysis.id, dto).subscribe({
      next: () => {
        this.saving = false;
        this.saveSuccess = 'Enregistré.';
        this.resultChanged.emit();
        setTimeout(() => (this.saveSuccess = null), 2500);
      },
      error: (err) => {
        this.saving = false;
        if (err.status === 403) {
          this.saveError = "Vous n'avez pas l'autorisation de modifier les résultats du calcul.";
        } else if (err.status === 409) {
          this.saveError = 'Conflit de mise à jour concurrente - veuillez recharger.';
        } else if (err.status === 400) {
          this.saveError = err.error?.message || 'Saisie invalide.';
        } else {
          this.saveError = err.error?.message || "Échec de l'enregistrement.";
        }
      }
    });
  }

  private buildPatchDto(): UpdateStoneResultDto {
    const v = this.form.getRawValue();
    const dto: UpdateStoneResultDto = {};
    for (const key of Object.keys(v) as (keyof UpdateStoneResultDto)[]) {
      const formValue: string = (v[key] ?? '').toString();
      const originalValue: string = ((this.result?.[key as keyof StoneResultDto] as string | null) ?? '').toString();
      if (formValue === originalValue) {
        continue;
      }
      // Backend semantics: null = no change; "" = explicit clear; non-empty = set value.
      dto[key] = formValue === '' ? '' : formValue;
    }
    return dto;
  }

  fieldValue(key: keyof StoneResultDto): string {
    const v = this.result?.[key];
    return (v as string) || '-';
  }
}
