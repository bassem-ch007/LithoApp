export type DrainageType = 'JJ' | 'URETERAL' | 'NEPHROSTOMY';

export type DrainageSide = 'LEFT' | 'RIGHT' | 'BILATERAL';

export type DrainageStatus = 'ACTIVE' | 'REMOVED';

export type JJType = 'STANDARD_6F' | 'LARGE_7F' | 'BIODEGRADABLE' | 'METALLIC';

export const DRAINAGE_TYPES: DrainageType[] = ['JJ', 'URETERAL', 'NEPHROSTOMY'];
export const DRAINAGE_SIDES: DrainageSide[] = ['LEFT', 'RIGHT', 'BILATERAL'];
export const JJ_TYPES: JJType[] = ['STANDARD_6F', 'LARGE_7F', 'BIODEGRADABLE', 'METALLIC'];

export const DRAINAGE_TYPE_LABELS: Record<DrainageType, string> = {
  JJ: 'Sonde JJ',
  URETERAL: 'Cathéter urétéral',
  NEPHROSTOMY: 'Néphrostomie'
};

export const DRAINAGE_SIDE_LABELS: Record<DrainageSide, string> = {
  LEFT: 'Gauche',
  RIGHT: 'Droite',
  BILATERAL: 'Bilatéral'
};

export const JJ_TYPE_LABELS: Record<JJType, string> = {
  STANDARD_6F: 'Standard (6 Fr)',
  LARGE_7F: 'Grand calibre (7 Fr)',
  BIODEGRADABLE: 'Biodégradable',
  METALLIC: 'Métallique (permanente)'
};

export const DRAINAGE_STATUS_LABELS: Record<DrainageStatus | 'OVERDUE', string> = {
  ACTIVE: 'Actif',
  REMOVED: 'Retiré',
  OVERDUE: 'En retard'
};

export interface CreateDrainageRequest {
  episodeId: number;
  patientId: number;
  drainageType: DrainageType;
  side: DrainageSide;
  placedAt: string;
  plannedRemovalDate?: string | null;
  jjType?: JJType | null;
  notes?: string | null;
}

export interface UpdateDrainageRequest {
  plannedRemovalDate?: string | null;
  notes?: string | null;
}

export interface RemoveDrainageRequest {
  removedAt: string;
}

export interface DrainageResponse {
  id: string;
  episodeId: number;
  patientId: number;
  doctorUsername: string | null;
  drainageType: DrainageType;
  side: DrainageSide;
  placedAt: string;
  plannedRemovalDate: string | null;
  removedAt: string | null;
  status: DrainageStatus;
  jjType: JJType | null;
  notes: string | null;
  overdue: boolean;
  preReminderSentAt: string | null;
  dayOfReminderSentAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface DrainageFilter {
  episodeId?: number;
  patientId?: number;
  drainageType?: DrainageType;
  status?: DrainageStatus;
  overdue?: boolean;
}
