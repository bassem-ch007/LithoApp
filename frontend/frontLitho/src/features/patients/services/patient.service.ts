import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreatePatientRequest,
  UpdatePatientRequest,
  PatientResponse,
  PatientSummaryResponse,
  Page
} from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private readonly baseUrl = `${environment.apiBaseUrl}/patients`;

  constructor(private http: HttpClient) {}

  searchPatients(
    filters: { di?: string; dmi?: string; name?: string; phone?: string },
    page: number = 0,
    size: number = 20
  ): Observable<Page<PatientSummaryResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'lastName');

    if (filters.di) params = params.set('di', filters.di);
    if (filters.dmi) params = params.set('dmi', filters.dmi);
    if (filters.name) params = params.set('name', filters.name);
    if (filters.phone) params = params.set('phone', filters.phone);

    return this.http.get<Page<PatientSummaryResponse>>(`${this.baseUrl}/search`, { params });
  }

  getPatientById(id: number): Observable<PatientResponse> {
    return this.http.get<PatientResponse>(`${this.baseUrl}/${id}`);
  }

  getPatientByDi(di: string): Observable<PatientResponse> {
    return this.http.get<PatientResponse>(`${this.baseUrl}/by-di/${di}`);
  }

  getPatientByDmi(dmi: string): Observable<PatientResponse> {
    return this.http.get<PatientResponse>(`${this.baseUrl}/by-dmi/${dmi}`);
  }

  createPatient(request: CreatePatientRequest): Observable<PatientResponse> {
    return this.http.post<PatientResponse>(this.baseUrl, request);
  }

  updatePatient(id: number, request: UpdatePatientRequest): Observable<PatientResponse> {
    return this.http.put<PatientResponse>(`${this.baseUrl}/${id}`, request);
  }
}
