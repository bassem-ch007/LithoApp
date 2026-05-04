import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AnalysisRequestDto,
  METABOLIC_DOCUMENT_LABELS,
  METABOLIC_DOCUMENT_TYPES,
  MetabolicDocumentType,
  MetabolicResultDto,
  PdfDocumentDto
} from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';

interface SlotState {
  uploading: boolean;
  error: string | null;
}

@Component({
  selector: 'app-metabolic-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './metabolic-panel.component.html',
  styleUrl: './metabolic-panel.component.scss'
})
export class MetabolicPanelComponent {
  @Input({ required: true }) analysis!: AnalysisRequestDto;
  @Input() result: MetabolicResultDto | null = null;
  @Input() canEdit = false;
  @Output() resultChanged = new EventEmitter<void>();

  readonly documentTypes = METABOLIC_DOCUMENT_TYPES;
  readonly labels = METABOLIC_DOCUMENT_LABELS;

  slotStates: Record<string, SlotState> = {};

  private analysisService = inject(AnalysisService);

  getActive(type: MetabolicDocumentType): PdfDocumentDto | null {
    return this.result?.latestDocuments?.find(d => d.documentType === type) ?? null;
  }

  getVersions(type: MetabolicDocumentType): PdfDocumentDto[] {
    return (this.result?.versionHistory ?? [])
      .filter(d => d.documentType === type)
      .sort((a, b) => b.versionNumber - a.versionNumber);
  }

  getState(type: MetabolicDocumentType): SlotState {
    if (!this.slotStates[type]) {
      this.slotStates[type] = { uploading: false, error: null };
    }
    return this.slotStates[type];
  }

  onFileSelected(type: MetabolicDocumentType, evt: Event): void {
    const input = evt.target as HTMLInputElement;
    const file = input.files && input.files[0];
    if (!file) return;
    this.upload(type, file, input);
  }

  private upload(type: MetabolicDocumentType, file: File, input: HTMLInputElement): void {
    if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
      this.getState(type).error = 'Seuls les fichiers PDF sont acceptés.';
      input.value = '';
      return;
    }

    const state = this.getState(type);
    state.uploading = true;
    state.error = null;

    this.analysisService.uploadMetabolicDocument(this.analysis.id, type, file).subscribe({
      next: () => {
        state.uploading = false;
        input.value = '';
        this.resultChanged.emit();
      },
      error: (err) => {
        state.uploading = false;
        input.value = '';
        if (err.status === 403) {
          state.error = "Vous n'avez pas l'autorisation de téléverser des documents.";
        } else if (err.status === 400) {
          state.error = err.error?.message || 'Fichier invalide.';
        } else if (err.status === 409) {
          state.error = 'Cette demande est déjà terminée.';
        } else {
          state.error = err.error?.message || 'Échec du téléversement.';
        }
      }
    });
  }

  download(type: MetabolicDocumentType, version?: number): void {
    const obs = version
      ? this.analysisService.downloadMetabolicDocumentVersion(this.analysis.id, type, version)
      : this.analysisService.downloadMetabolicDocument(this.analysis.id, type);

    obs.subscribe({
      next: (blob) => {
        const doc = this.getActive(type);
        const filename = version
          ? `${type}_v${version}.pdf`
          : (doc?.originalFilename ?? `${type}.pdf`);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.getState(type).error = 'Échec du téléchargement du document.';
      }
    });
  }

  formatSize(bytes: number): string {
    if (!bytes && bytes !== 0) return '';
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
