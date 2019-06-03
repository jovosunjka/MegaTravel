import { Injectable, Injector, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class GenericService {
  
  constructor(private http: HttpClient, @Inject('SIEM_CENTER_API_URL') private baseUrl: string) {}

  getAll<T>(relativeUrl: string): Observable<T[]> {
    return this.http.get<T[]>(this.baseUrl + relativeUrl);
  }

  get<T>(relativeUrl: string): Observable<T> {
    // const params: HttpParams = new HttpParams().set('_id',id);
    return this.http.get<T>(this.baseUrl + relativeUrl);
  }

  getById<T>(relativeUrl: string, id: number): Observable<T> {
    // const params: HttpParams = new HttpParams().set('_id',id);
    return this.http.get<T>(this.baseUrl + relativeUrl + `/${id}`);
  }

  getListById<T>(relativeUrl: string, id: number): Observable<T[]> {
    // const params: HttpParams = new HttpParams().set('_id',id);
    return this.http.get<T[]>(this.baseUrl + relativeUrl + `/${id}`);
  }

  put<T>(relativeUrl: string, t: T): Observable<T> {
    const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.put<T>(this.baseUrl + relativeUrl, t, { headers });
  }

  save<T>(relativeUrl: string, t: T) {
    const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post(this.baseUrl + relativeUrl, t,  { headers });
  }

  post(relativeUrl: string, t: any): any {
    const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post(this.baseUrl + relativeUrl, t, { headers });
  }

  delete(relativeUrl: string, id: number) {
    return this.http.delete(this.baseUrl + relativeUrl + `/${id}` + '/delete');
  }

  deletePermissionFromRole(relativeUrl: string, roleId: number, permissinId: number) {
    return this.http.delete(this.baseUrl + relativeUrl + `/${roleId}/${permissinId}`);
  }




}
