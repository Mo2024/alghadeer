import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SubTopicService {

  private apiUrl = environment.apiUrl;

  url = `${this.apiUrl}/api/sub-topics`;

  constructor(private http: HttpClient) { }
  
  createSubTopic(body: any): Observable<any> {

    return this.http.post(`${this.url}/all/create-sub-topic`, body, { withCredentials: true });
  }

  editSubTopic(body: any): Observable<any> {
    return this.http.put(`${this.url}/all/edit-sub-topic`, body, { withCredentials: true });
  }


  deleteSubTopic(body: any): Observable<any> {
    return this.http.request<any>('DELETE', `${this.url}/all/delete-sub-topic`, {
      body,
      withCredentials: true
    });
  }
}
