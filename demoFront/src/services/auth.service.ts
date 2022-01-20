import { Injectable } from '@angular/core';
import {HttpClient,HttpParams, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
	private url = "http://localhost:8083";
  constructor(private http:HttpClient) { }
	login(name: string): Observable<string>{
		let params: HttpParams = new HttpParams();
		params = params.set('name', name);
		return this.http.post<string>(`${this.url}/login`, params, { responseType:  'text' as 'json'});
	}
	
	logout(): Observable<any>{
		var name = localStorage.getItem('name');
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.get(`${this.url}/logout`, {'headers': headers});
		} else {
			return this.http.get(`${this.url}/logout`);
		}
		
	}
}
