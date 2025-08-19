import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {


  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/announcement`;

  constructor(private http: HttpClient) { }

  createAnnouncement(body: any): Observable<any> {
    return this.http.post(`${this.url}/supervisor/create-announcement`, body, { withCredentials: true });
  }
}
