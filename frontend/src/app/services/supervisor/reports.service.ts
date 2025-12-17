import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/reports`;

  constructor(private http: HttpClient) { }
  getEnrolledStudentsTelephone(semesterId: any): Observable<Blob> {
    return this.http.get(`${this.url}/supervisor/get-enrolled-students-telephone?semesterId=${semesterId}`, {
      responseType: 'blob',
      withCredentials: true
    });
  }


}
