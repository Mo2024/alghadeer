import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/students`;

  constructor(private http: HttpClient) { }

  login(body: any): Observable<any> {
    return this.http.post(`${this.url}/login`, body, { withCredentials: true });
  }

  register(body: any): Observable<any> {
    return this.http.post(`${this.url}/register`, body, { withCredentials: true });
  }

}
