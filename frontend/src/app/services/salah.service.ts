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

  getSubjectsByLevel(studentId: any): Observable<any> {
    return this.http.get(`${this.url}/subjects/all/subjects-by-level?studentId=${studentId}`, { withCredentials: true });
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


  // attempt

  getStudentAttempt(studentId: any): Observable<any> {
    return this.http.get(`${this.url}/attempt/all/latest-attempt?studentId=${studentId}`, { withCredentials: true });
  }

  getAttemptQuestions(attemptId: any): Observable<any> {
    return this.http.get(`${this.url}/attempt/all/attempt-questions?attemptId=${attemptId}`, { withCredentials: true });
  }

  createAttempt(body: any, studentId: any): Observable<any> {
    return this.http.post(`${this.url}/attempt/all/create-attempt?studentId=${studentId}`, body, { withCredentials: true });
  }

  submitAttempt(body: any): Observable<any> {
    return this.http.post(`${this.url}/attempt/all/submit-questions`, body, { withCredentials: true });
  }

  saveAttempt(body: any): Observable<any> {
    return this.http.put(`${this.url}/attempt/all/save-questions`, body, { withCredentials: true });
  }

  //Student level
  updateLevel(body: any): Observable<any> {
    return this.http.put(`${this.url}/level/all/update-level`, body, { withCredentials: true });
  }
}
