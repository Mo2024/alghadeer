import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/staff`;

  constructor(private http: HttpClient) { }

  getAuth(body: any): Observable<any> {
    return this.http.get(`${this.url}/get-auth`, { withCredentials: true });
  }

  login(body: any): Observable<any> {
    return this.http.post(`${this.url}/login`, body, { withCredentials: true });
  }

  register(body: any): Observable<any> {
    return this.http.post(`${this.url}/admin/register`, body, { withCredentials: true });
  }

  archive(body: any): Observable<any> {
    return this.http.post(`${this.url}/admin/archive`, body, { withCredentials: true });
  }
}
