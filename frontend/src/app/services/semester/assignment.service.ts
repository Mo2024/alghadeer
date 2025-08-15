import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/assignment`;

  constructor(private http: HttpClient) { }

  createAssignment(body: any, classId: any): Observable<any> {
    return this.http.post(`${this.url}/all/create-assignment?classId=${classId}`, body, { withCredentials: true });
  }
}
