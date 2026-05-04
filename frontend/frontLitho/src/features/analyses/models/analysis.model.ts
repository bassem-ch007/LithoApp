export type AnalysisType = 'METABOLIC' | 'STONE';

export type AnalysisStatus = 'CREATED' | 'IN_PROGRESS' | 'COMPLETED';

export type MetabolicDocumentType = 'BLOOD_TEST' | 'MORNING_URINE' | 'H24_URINE';

export type AuditActionType =
  | 'REQUEST_CREATED'
  | 'STATUS_CHANGED'
  | 'PDF_UPLOADED'
  | 'PDF_REPLACED'
  | 'STONE_RESULT_CREATED'
  | 'STONE_RESULT_FIELD_UPDATED'
  | 'REQUEST_COMPLETED';

export const METABOLIC_DOCUMENT_TYPES: MetabolicDocumentType[] = [
  'BLOOD_TEST',
  'MORNING_URINE',
  'H24_URINE'
];

export const METABOLIC_DOCUMENT_LABELS: Record<MetabolicDocumentType, string> = {
  BLOOD_TEST: 'Bilan sanguin',
  MORNING_URINE: 'Urines du matin',
  H24_URINE: 'Urines de 24h'
};

export const ANALYSIS_TYPE_LABELS: Record<AnalysisType, string> = {
  METABOLIC: 'Métabolique',
  STONE: 'Calcul'
};

export const ANALYSIS_STATUS_LABELS: Record<AnalysisStatus, string> = {
  CREATED: 'Créée',
  IN_PROGRESS: 'En cours',
  COMPLETED: 'Terminée'
};

export interface CreateAnalysisRequestDto {
  patientId: number;
  episodeId: number;
  type: AnalysisType;
}

export interface UpdateStoneResultDto {
  morphSize?: string | null;
  morphSurface?: string | null;
  morphColor?: string | null;
  morphSection?: string | null;
  morphOuterLayers?: string | null;
  morphCore?: string | null;
  spectroSurface?: string | null;
  spectroSection?: string | null;
  spectroOuterLayers?: string | null;
  spectroCore?: string | null;
  finalStoneType?: string | null;
}

export interface PdfDocumentDto {
  id: number;
  documentType: MetabolicDocumentType;
  versionNumber: number;
  active: boolean;
  originalFilename: string;
  fileSizeBytes: number;
  uploadedBy: string;
  uploadedAt: string;
}

export interface MetabolicResultDto {
  id: number | null;
  analysisRequestId: number;
  latestDocuments: PdfDocumentDto[];
  versionHistory: PdfDocumentDto[];
  uploadedTypesCount: number;
}

export interface StoneResultDto {
  id: number | null;
  analysisRequestId: number;
  morphSize: string | null;
  morphSurface: string | null;
  morphColor: string | null;
  morphSection: string | null;
  morphOuterLayers: string | null;
  morphCore: string | null;
  spectroSurface: string | null;
  spectroSection: string | null;
  spectroOuterLayers: string | null;
  spectroCore: string | null;
  finalStoneType: string | null;
  lastModifiedBy: string | null;
  lastModifiedAt: string | null;
  createdAt: string | null;
  version: number | null;
}

export interface AnalysisRequestDto {
  id: number;
  episodeId: number;
  patientId: number;
  createdBy: string;
  type: AnalysisType;
  status: AnalysisStatus;
  createdAt: string;
  completedAt: string | null;
  completedBy: string | null;
  version: number | null;
  metabolicResult: MetabolicResultDto | null;
  stoneResult: StoneResultDto | null;
}

export interface AuditEntryDto {
  id: number;
  analysisRequestId: number;
  actorId: string;
  actionType: AuditActionType;
  targetField: string | null;
  oldValue: string | null;
  newValue: string | null;
  timestamp: string;
}

export interface AnalysisSearchFilters {
  di?: string;
  dmi?: string;
  name?: string;
  phone?: string;
  status?: AnalysisStatus;
}
