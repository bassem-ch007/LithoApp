export type NotificationType =
  | 'ANALYSIS_CREATED'
  | 'ANALYSIS_STARTED'
  | 'ANALYSIS_RESULT_ADDED'
  | 'ANALYSIS_COMPLETED'
  | 'DRAINAGE_CREATED'
  | 'DRAINAGE_REMOVAL_SOON'
  | 'DRAINAGE_OVERDUE'
  | 'DRAINAGE_REMOVED'
  | 'SYSTEM';

export type NotificationStatus = 'UNREAD' | 'READ' | 'DELIVERED' | 'FAILED';

export type NotificationChannel = 'IN_APP' | 'EMAIL' | 'SMS';

export type NotificationReferenceType =
  | 'PATIENT'
  | 'EPISODE'
  | 'ANALYSIS'
  | 'DRAINAGE'
  | 'SYSTEM';

export interface AppNotification {
  id: string;
  recipientUsername: string | null;
  recipientRole: string | null;
  type: NotificationType;
  channel: NotificationChannel;
  status: NotificationStatus;
  title: string;
  message: string;
  referenceType: NotificationReferenceType | null;
  referenceId: string | null;
  createdAt: string;
  readAt: string | null;
}

export interface NotificationPage {
  content: AppNotification[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface UnreadCountResponse {
  unread: number;
}

export const NOTIFICATION_TYPE_LABELS: Record<NotificationType, string> = {
  ANALYSIS_CREATED: 'Demande d’analyse créée',
  ANALYSIS_STARTED: 'Analyse démarrée',
  ANALYSIS_RESULT_ADDED: 'Résultat ajouté',
  ANALYSIS_COMPLETED: 'Analyse terminée',
  DRAINAGE_CREATED: 'Drainage créé',
  DRAINAGE_REMOVAL_SOON: 'Retrait à prévoir',
  DRAINAGE_OVERDUE: 'Drainage en retard',
  DRAINAGE_REMOVED: 'Drainage retiré',
  SYSTEM: 'Système'
};
