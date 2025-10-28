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


  // questions

  getQuestions(level: string): Observable<any> {
    return this.http.get(`${this.url}/questions/all/get-questions-by-level?level=${level}`, { withCredentials: true });
  }

  editQuestion(body: any, level: string): Observable<any> {
    return this.http.put(`${this.url}/questions/admin/edit-question?level=${level}`, body, { withCredentials: true });
  }

  createQuestion(body: any, level: string): Observable<any> {
    return this.http.post(`${this.url}/questions/admin/create-question?level=${level}`, body, { withCredentials: true });
  }

  deleteQuestion(level: string, questionId: any): Observable<any> {
    return this.http.delete(`${this.url}/questions/admin/delete-question?level=${level}&questionId=${questionId}`, { withCredentials: true });
  }
}
