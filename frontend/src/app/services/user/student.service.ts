import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StudentService {


  private apiUrl = this.setApiUrl(window.location.hostname);


  setApiUrl(hostname: string) {

    if (!environment.production && hostname.startsWith('stu.')) {
      return environment.studentApiUrl
    } else {
      return environment.apiUrl
    }
  }

  url = `${this.apiUrl}/api/students`;

  constructor(private http: HttpClient) { }

  getAuth(body: any): Observable<any> {
    return this.http.get(`${this.url}/get-auth`, {
      headers: new HttpHeaders({
        'Cache-Control': 'no-cache',
        'Pragma': 'no-cache',
        'Expires': '0'
      }),
      withCredentials: true
    });
  }

  getEnrolledStudents(): Observable<any> {
    return this.http.get(`${this.url}/supervisor/get-enrolled-students`, { withCredentials: true });
  }

  getStudentPageDetails(): Observable<any> {
    return this.http.get(`${this.url}/student/get-student-page-details`, { withCredentials: true });
  }

  login(body: any): Observable<any> {
    return this.http.post(`${this.url}/login`, body, { withCredentials: true });
  }

  register(body: any): Observable<any> {
    return this.http.post(`${this.url}/register`, body, { withCredentials: true });
  }

  getStudentsByClassId(classId: any): Observable<any> {
    return this.http.get(`${this.url}/all/students-by-class-id?classId=${classId}`, { withCredentials: true });
  }

}
