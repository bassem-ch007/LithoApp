import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateDrainageRequest,
  DrainageFilter,
  DrainageResponse,
  RemoveDrainageRequest,
  UpdateDrainageRequest
} from '../models/drainage.model';

@Injectable({
  providedIn: 'root'
})
export class DrainageService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/drainages`;

  constructor(private http: HttpClient) {}

  create(request: CreateDrainageRequest): Observable<DrainageResponse> {
    return this.http.post<DrainageResponse>(this.baseUrl, request);
  }

  getById(id: string): Observable<DrainageResponse> {
    return this.http.get<DrainageResponse>(`${this.baseUrl}/${id}`);
  }

  listByEpisode(episodeId: number): Observable<DrainageResponse[]> {
    return this.http.get<DrainageResponse[]>(`${this.baseUrl}/episode/${episodeId}`);
  }

  listByPatient(patientId: number): Observable<DrainageResponse[]> {
    return this.http.get<DrainageResponse[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  list(filter: DrainageFilter = {}): Observable<DrainageResponse[]> {
    let params = new HttpParams();
    if (filter.episodeId != null) params = params.set('episodeId', filter.episodeId.toString());
    if (filter.patientId != null) params = params.set('patientId', filter.patientId.toString());
    if (filter.drainageType) params = params.set('drainageType', filter.drainageType);
    if (filter.status) params = params.set('status', filter.status);
    if (filter.overdue != null) params = params.set('overdue', String(filter.overdue));
    return this.http.get<DrainageResponse[]>(this.baseUrl, { params });
  }

  update(id: string, request: UpdateDrainageRequest): Observable<DrainageResponse> {
    return this.http.put<DrainageResponse>(`${this.baseUrl}/${id}`, request);
  }

  remove(id: string, request: RemoveDrainageRequest): Observable<DrainageResponse> {
    return this.http.patch<DrainageResponse>(`${this.baseUrl}/${id}/remove`, request);
  }
}
