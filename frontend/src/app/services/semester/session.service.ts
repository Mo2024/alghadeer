import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  private apiUrl = environment.apiUrl;
  private pageSize = environment.pageSize;

  url = `${this.apiUrl}/api/session`;

  constructor(private http: HttpClient) { }

  changeSessionSubTopic(body: any): Observable<any> {
    return this.http.put(`${this.url}/all/change-sub-topic`, body, { withCredentials: true });
  }

  getUpcomingSessions(page: any): Observable<any> {
    return this.http.get(`${this.url}/all/upcoming-sessions?page=${page}&size=${this.pageSize}`, { withCredentials: true });
  }

  getSessionsByActiveSemester(): Observable<any> {
    return this.http.get(`${this.url}/all/get-sessions-by-active-semester`, { withCredentials: true });
  }

  getSessionsDates(): Observable<any> {
    return this.http.get(`${this.url}/all/get-sessions-dates`, { withCredentials: true });
  }

  getAttendanceStatus(body: any): Observable<any> {
    return this.http.get(`${this.url}/all/get-attendance-status`, {
      params: body,
      withCredentials: true
    });
  }

  takeAttendance(body: any): Observable<any> {
    return this.http.post(`${this.url}/all/take-attendance`, body, { withCredentials: true });
  }

  cancelSessionsBySessionId(body: any): Observable<any> {
    return this.http.post(`${this.url}/all/cancel-sessions-by-session-id`, body, { withCredentials: true });
  }

  cancelSessionsByDates(body: any): Observable<any> {
    return this.http.post(`${this.url}/all/cancel-sessions-by-dates`, body, { withCredentials: true });
  }

}
