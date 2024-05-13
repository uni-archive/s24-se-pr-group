import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';
import { News } from "../dtos/news";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsBaseUri: string = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all news from the backend
   */
  getNews(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri);
  }

  /**
   * Loads specific news from the backend
   *
   * @param id of news to load
   */
  getNewsById(id: number): Observable<News> {
    console.log('Load news details for ' + id);
    return this.httpClient.get<News>(this.newsBaseUri + '/' + id);
  }

  /**
   * Persists news to the backend
   *
   * @param formData FormData object containing news data including image
   */
  /**createNews(formData: FormData): Observable<News> {
   console.log('Create news');

   const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');

   return this.httpClient.post<News>(this.newsBaseUri, formData, { headers });
   }
   */


  createNews(formData: FormData): Observable<News> {
    console.log('Create news', formData);
    return this.httpClient.post<News>(this.newsBaseUri, formData);
  }









  createNews2(formData: FormData): Observable<News> {
    console.log('Create news');

    const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');

    return this.httpClient.post<News>(this.newsBaseUri, formData, {headers});
  }
}
