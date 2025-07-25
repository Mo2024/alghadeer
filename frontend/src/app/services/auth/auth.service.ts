import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/auth`;

  constructor(private http: HttpClient) { }

  getAuth(): Observable<any> {
    return this.http.get(`${this.url}/get-auth`, { withCredentials: true });
  }

  logout(): Observable<any> {
    return this.http.post(`${this.url}/logout`, {}, { withCredentials: true });
  }

}
