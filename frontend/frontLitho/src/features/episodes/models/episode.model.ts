import { Page } from '../../patients/models/patient.model';

export type EpisodeStatus = 'ACTIVE' | 'CLOSED';

export interface CreateEpisodeRequest {
  patientId: number;
  openedAt: string;
  title?: string | null;
  notes?: string | null;
  recurrence: boolean;
}

export interface UpdateEpisodeRequest {
  title?: string | null;
  notes?: string | null;
  recurrence?: boolean | null;
  status?: EpisodeStatus | null;
}

export interface EpisodeResponse {
  id: number;
  patientId: number;
  status: EpisodeStatus;
  openedAt: string;
  title: string | null;
  notes: string | null;
  recurrence: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface EpisodeSummaryResponse {
  id: number;
  patientId: number;
  status: EpisodeStatus;
  openedAt: string;
  title: string | null;
  recurrence: boolean;
  createdAt: string;
}

export type EpisodePage = Page<EpisodeSummaryResponse>;
