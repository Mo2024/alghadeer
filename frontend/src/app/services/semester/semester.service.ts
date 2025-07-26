import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SemesterService {

  private apiUrl = environment.apiUrl;
  private pageSize = environment.pageSize;

  url = `${this.apiUrl}/api/semester`;

  constructor(private http: HttpClient) { }

  closeSemester(body: any): Observable<any> {
    return this.http.put(`${this.url}/admin/close-semester?page=${body.page}&size=${body.size}`, null, { withCredentials: true });
  }

  isEnrolled(): Observable<any> {
    return this.http.get(`${this.url}/student/is-enrolled`, { withCredentials: true });
  }

  getSemesters(page: number): Observable<any> {
    return this.http.get(`${this.url}/admin/get-semesters?page=${page}&size=${this.pageSize}`, { withCredentials: true });
  }

  enrollStudent(body: any): Observable<any> {
    return this.http.post(`${this.url}/student/enroll`, body, { withCredentials: true });
  }

  createSemester(body: any): Observable<any> {
    return this.http.post(`${this.url}/admin/create`, body, { withCredentials: true });
  }

}
