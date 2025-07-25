import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private apiUrl = environment.apiUrl;
  private pageSize = environment.pageSize;

  url = `${this.apiUrl}/api/staff`;

  constructor(private http: HttpClient) { }

  getAuth(): Observable<any> {
    return this.http.get(`${this.url}/get-auth`, {
      headers: new HttpHeaders({
        'Cache-Control': 'no-cache',
        'Pragma': 'no-cache',
        'Expires': '0'
      }),
      withCredentials: true
    });
  }


  login(body: any): Observable<any> {
    return this.http.post(`${this.url}/login`, body, { withCredentials: true });
  }

  getStaff(page: number): Observable<any> {
    return this.http.get(`${this.url}/admin/get-staff?page=${page}&size=${this.pageSize}`, { withCredentials: true });
  }

  register(body: any): Observable<any> {
    return this.http.post(`${this.url}/admin/register`, body, { withCredentials: true });
  }

  archive(body: any): Observable<any> {
    return this.http.put(`${this.url}/admin/archive`, body, { withCredentials: true });
  }
}
