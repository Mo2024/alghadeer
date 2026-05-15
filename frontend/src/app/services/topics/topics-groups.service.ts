import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TopicsGroupsService {


  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/topics-groups`;

  constructor(private http: HttpClient) { }

  getTopicsGroups(): Observable<any> {

    return this.http.get(`${this.url}/all/get-topics-groups`, { withCredentials: true });
  }
  createTopicGroup(body: any): Observable<any> {

    return this.http.post(`${this.url}/all/create-topic-group`, body, { withCredentials: true });
  }

  editTopicGroup(body: any): Observable<any> {
    return this.http.put(`${this.url}/all/edit-topic-group`, body, { withCredentials: true });
  }


  deleteTopicGroup(body: any): Observable<any> {
    return this.http.request<any>('DELETE', `${this.url}/all/delete-topic-group`, {
      body,
      withCredentials: true
    });
  }

}
