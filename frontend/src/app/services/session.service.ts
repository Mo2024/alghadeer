import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/session`;

  constructor(private http: HttpClient) { }

  changeSessionSubTopic(body: any): Observable<any> {
    return this.http.put(`${this.url}/instructor/change-sub-topic`, body, { withCredentials: true });
  }

  takeAttendance(body: any): Observable<any> {
    return this.http.post(`${this.url}/instructor/take-attendance`, body, { withCredentials: true });
  }

  cancelSessions(body: any): Observable<any> {
    return this.http.post(`${this.url}/instructor/cancel-sessions`, body, { withCredentials: true });
  }
}
