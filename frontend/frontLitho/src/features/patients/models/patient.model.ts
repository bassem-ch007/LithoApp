export type Gender = 'MALE' | 'FEMALE';
export type KidneyType = 'ANATOMICAL' | 'FUNCTIONAL';

export interface ClinicalInfoRequest {
  familyHistory?: boolean | null;
  personalHistory?: boolean | null;
  lastEpisodeDate?: string | null;
  lithiasisType?: string | null;
  frequentInfections?: boolean | null;
  singleKidney?: boolean | null;
  kidneyType?: KidneyType | null;
  chronicRenalFailure?: boolean | null;
  clearance?: number | null;
}

export interface MedicationRequest {
  hasMedication: boolean;
  description?: string | null;
}

export interface CreatePatientRequest {
  di: string;
  dmi: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: Gender;
  height?: number | null;
  weight?: number | null;
  address?: string | null;
  email?: string | null;
  phone?: string | null;
  clinicalInfo?: ClinicalInfoRequest | null;
  medication?: MedicationRequest | null;
  associatedDiseases?: string[] | null;
  geneticDiseases?: string[] | null;
  anatomicalAnomalies?: string[] | null;
}

export interface UpdatePatientRequest {
  firstName?: string | null;
  lastName?: string | null;
  birthDate?: string | null;
  gender?: Gender | null;
  height?: number | null;
  weight?: number | null;
  address?: string | null;
  email?: string | null;
  phone?: string | null;
  clinicalInfo?: ClinicalInfoRequest | null;
  medication?: MedicationRequest | null;
  associatedDiseases?: string[] | null;
  geneticDiseases?: string[] | null;
  anatomicalAnomalies?: string[] | null;
}

export interface ClinicalInfoResponse {
  id: number;
  familyHistory: boolean | null;
  personalHistory: boolean | null;
  lastEpisodeDate: string | null;
  lithiasisType: string | null;
  frequentInfections: boolean | null;
  singleKidney: boolean | null;
  kidneyType: KidneyType | null;
  chronicRenalFailure: boolean | null;
  clearance: number | null;
}

export interface MedicationResponse {
  id: number;
  hasMedication: boolean;
  description: string | null;
}

export interface PatientResponse {
  id: number;
  di: string;
  dmi: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: Gender;
  height: number | null;
  weight: number | null;
  address: string | null;
  email: string | null;
  phone: string | null;
  createdAt: string;
  updatedAt: string;
  clinicalInfo: ClinicalInfoResponse | null;
  medication: MedicationResponse | null;
  associatedDiseases: string[];
  geneticDiseases: string[];
  anatomicalAnomalies: string[];
}

export interface PatientSummaryResponse {
  id: number;
  di: string;
  dmi: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: Gender;
  phone: string | null;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
