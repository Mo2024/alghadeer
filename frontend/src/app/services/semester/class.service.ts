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
}
