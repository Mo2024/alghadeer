import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SalahService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/salah`;

  constructor(private http: HttpClient) { }


  getSubjects(): Observable<any> {
    return this.http.get(`${this.url}/subjects/admin/get-subjects`, { withCredentials: true });
  }

  createSubject(body: any): Observable<any> {
    return this.http.post(`${this.url}/subjects/admin/create-subject`, body, { withCredentials: true });
  }

  createArea(body: any): Observable<any> {
    return this.http.post(`${this.url}/subjects/admin/create-subject-area`, body, { withCredentials: true });
  }

  editSubject(body: any): Observable<any> {
    return this.http.put(`${this.url}/subjects/admin/edit-subject`, body, { withCredentials: true });
  }

  editArea(body: any): Observable<any> {
    return this.http.put(`${this.url}/subjects/admin/edit-subject-area`, body, { withCredentials: true });
  }

}
