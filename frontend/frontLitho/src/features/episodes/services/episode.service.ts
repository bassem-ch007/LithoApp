import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateEpisodeRequest,
  EpisodePage,
  EpisodeResponse,
  EpisodeStatus,
  UpdateEpisodeRequest
} from '../models/episode.model';

@Injectable({
  providedIn: 'root'
})
export class EpisodeService {
  private readonly baseUrl = `${environment.apiBaseUrl}/episodes`;

  constructor(private http: HttpClient) {}

  getEpisodesByPatient(
    patientId: number,
    page: number = 0,
    size: number = 20,
    status?: EpisodeStatus
  ): Observable<EpisodePage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'openedAt,desc');

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<EpisodePage>(`${this.baseUrl}/patient/${patientId}`, { params });
  }

  getEpisodeById(id: number): Observable<EpisodeResponse> {
    return this.http.get<EpisodeResponse>(`${this.baseUrl}/${id}`);
  }

  createEpisode(request: CreateEpisodeRequest): Observable<EpisodeResponse> {
    return this.http.post<EpisodeResponse>(this.baseUrl, request);
  }

  updateEpisode(id: number, request: UpdateEpisodeRequest): Observable<EpisodeResponse> {
    return this.http.put<EpisodeResponse>(`${this.baseUrl}/${id}`, request);
  }
}
