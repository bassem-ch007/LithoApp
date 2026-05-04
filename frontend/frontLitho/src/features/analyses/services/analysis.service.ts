import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AnalysisRequestDto,
  AnalysisSearchFilters,
  AnalysisStatus,
  AuditEntryDto,
  CreateAnalysisRequestDto,
  MetabolicDocumentType,
  MetabolicResultDto,
  PdfDocumentDto,
  StoneResultDto,
  UpdateStoneResultDto
} from '../models/analysis.model';

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/analysis-requests`;

  constructor(private http: HttpClient) {}

  // ── Lifecycle ───────────────────────────────────────────────────────────

  createRequest(request: CreateAnalysisRequestDto): Observable<AnalysisRequestDto> {
    return this.http.post<AnalysisRequestDto>(this.baseUrl, request);
  }

  getById(id: number): Observable<AnalysisRequestDto> {
    return this.http.get<AnalysisRequestDto>(`${this.baseUrl}/${id}`);
  }

  listByEpisode(episodeId: number, status?: AnalysisStatus): Observable<AnalysisRequestDto[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<AnalysisRequestDto[]>(`${this.baseUrl}/episode/${episodeId}`, { params });
  }

  listByPatient(patientId: number, status?: AnalysisStatus): Observable<AnalysisRequestDto[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<AnalysisRequestDto[]>(`${this.baseUrl}/patient/${patientId}`, { params });
  }

  listByStatus(status: AnalysisStatus): Observable<AnalysisRequestDto[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<AnalysisRequestDto[]>(this.baseUrl, { params });
  }

  search(filters: AnalysisSearchFilters): Observable<AnalysisRequestDto[]> {
    let params = new HttpParams();
    if (filters.di) params = params.set('di', filters.di);
    if (filters.dmi) params = params.set('dmi', filters.dmi);
    if (filters.name) params = params.set('name', filters.name);
    if (filters.phone) params = params.set('phone', filters.phone);
    if (filters.status) params = params.set('status', filters.status);
    return this.http.get<AnalysisRequestDto[]>(`${this.baseUrl}/search`, { params });
  }

  complete(id: number): Observable<AnalysisRequestDto> {
    return this.http.post<AnalysisRequestDto>(`${this.baseUrl}/${id}/complete`, {});
  }

  // ── Metabolic ───────────────────────────────────────────────────────────

  getMetabolicResult(id: number): Observable<MetabolicResultDto> {
    return this.http.get<MetabolicResultDto>(`${this.baseUrl}/${id}/metabolic`);
  }

  uploadMetabolicDocument(
    id: number,
    documentType: MetabolicDocumentType,
    file: File
  ): Observable<PdfDocumentDto> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<PdfDocumentDto>(
      `${this.baseUrl}/${id}/metabolic/documents/${documentType}`,
      form
    );
  }

  downloadMetabolicDocument(id: number, documentType: MetabolicDocumentType): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/metabolic/documents/${documentType}/download`, {
      responseType: 'blob'
    });
  }

  downloadMetabolicDocumentVersion(
    id: number,
    documentType: MetabolicDocumentType,
    version: number
  ): Observable<Blob> {
    return this.http.get(
      `${this.baseUrl}/${id}/metabolic/documents/${documentType}/versions/${version}/download`,
      { responseType: 'blob' }
    );
  }

  // ── Stone ───────────────────────────────────────────────────────────────

  getStoneResult(id: number): Observable<StoneResultDto> {
    return this.http.get<StoneResultDto>(`${this.baseUrl}/${id}/stone`);
  }

  updateStoneResult(id: number, dto: UpdateStoneResultDto): Observable<StoneResultDto> {
    return this.http.patch<StoneResultDto>(`${this.baseUrl}/${id}/stone`, dto);
  }

  // ── Audit ───────────────────────────────────────────────────────────────

  getAuditLog(id: number): Observable<AuditEntryDto[]> {
    return this.http.get<AuditEntryDto[]>(`${this.baseUrl}/${id}/audit`);
  }
}
