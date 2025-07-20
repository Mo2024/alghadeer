import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/students`;

    private permissionsSubject = new BehaviorSubject<Map<string, boolean>>(new Map());
    permissions$ = this.permissionsSubject.asObservable();
  
    setPermissions(permissions: Map<string, boolean>) {
      const map = new Map<string, boolean>(Object.entries(permissions));
      this.permissionsSubject.next(map);
    }
  
    getPermissions(): Map<string, boolean> {
      return this.permissionsSubject.getValue();
    }
  
    hasPermission(permission: string): boolean {
      return this.getPermissions().get(permission) === true;
    }
  
    isPermissionsEmpty(): boolean {
    return this.permissionsSubject.getValue().size === 0;
  }


  constructor(private http: HttpClient) { }

  getAuth(body: any): Observable<any> {
    return this.http.get(`${this.url}/get-auth`, { withCredentials: true });
  }

  login(body: any): Observable<any> {
    return this.http.post(`${this.url}/login`, body, { withCredentials: true });
  }

  register(body: any): Observable<any> {
    return this.http.post(`${this.url}/register`, body, { withCredentials: true });
  }

}
