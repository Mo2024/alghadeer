import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MainTopicService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/main-topics`;

  constructor(private http: HttpClient) { }

  getTopics(): Observable<any> {

    return this.http.get(`${this.url}/all/get-topics`, { withCredentials: true });
  }
  createMainTopic(body: any): Observable<any> {

    return this.http.post(`${this.url}/all/create-main-topic`, body, { withCredentials: true });
  }

  editMainTopic(body: any): Observable<any> {
    return this.http.put(`${this.url}/all/edit-main-topic`, body, { withCredentials: true });
  }


  deleteMainTopic(body: any): Observable<any> {
    return this.http.request<any>('DELETE', `${this.url}/all/delete-main-topic`, {
      body,
      withCredentials: true
    });
  }

}
