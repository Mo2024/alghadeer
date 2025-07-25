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

  // getMainTopics(currentPage: number): Observable<any> {
  //   let params = selectedSite == '' ? `0?page=${currentPage}&size=10&isSiteQuery=false` : `${selectedSite}?page=${currentPage}&size=10&isSiteQuery=true`
  //   return this.http.get(`${this.url}/${params}`, { withCredentials: true });
  // }

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
