import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClassService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/class`;

  constructor(private http: HttpClient) { }

  changeStudentClass(body: any): Observable<any> {
    return this.http.post(`${this.url}/supervisor/change-student-class`, body, { withCredentials: true });
  }

  getAssignedClasses(withSessionsAndAssignments: boolean = true): Observable<any> {
    return this.http.get(`${this.url}/all/assigned-classes?withSessionsAndAssignments=${withSessionsAndAssignments}`, { withCredentials: true });
  }

  getActiveClasses(): Observable<any> {
    return this.http.get(`${this.url}/supervisor/active-classes-with-sessions`, { withCredentials: true });
  }

  getClassesByActiveSemester(): Observable<any> {
    return this.http.get(`${this.url}/supervisor/get-classes-active-semester`, { withCredentials: true });
  }

  getClassesBySemesterId(semesterId: number): Observable<any> {
    return this.http.get(`${this.url}/all/get-classes-by-semesterId?semesterId=${semesterId}`, { withCredentials: true });
  }
}
