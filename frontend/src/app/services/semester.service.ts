import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SemesterService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/semester`;

  constructor(private http: HttpClient) { }

  closeSemester(body: any): Observable<any> {
    return this.http.put(`${this.url}/admin/close-semester`, body, { withCredentials: true });
  }

  isEnrolled(): Observable<any> {
    return this.http.get(`${this.url}/student/is-enrolled`, { withCredentials: true });
  }

  enrollStudent(body: any): Observable<any> {
    return this.http.post(`${this.url}/student/enroll`, body, { withCredentials: true });
  }

  createSemester(body: any): Observable<any> {
    return this.http.post(`${this.url}/admin/create`, body, { withCredentials: true });
  }

}
