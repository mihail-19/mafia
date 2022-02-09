import { Injectable } from '@angular/core';
import {HttpClient,HttpParams, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
	private url = "http://localhost:8083";
  constructor(private http:HttpClient) { }
	login(name: string): Observable<string>{
		console.log('login');
		let params: HttpParams = new HttpParams();
		params = params.set('username', name);
		return this.http.post<string>(`${this.url}/login-process`, params, { responseType:  'text' as 'json'});
	}
	getName():Observable<string>{
		console.log('get name');
		return this.http.get<string>(`${this.url}/player-name`, { responseType:  'text' as 'json'});
	}
	
	logout(): Observable<string>{
		console.log('logout service');
		
		return this.http.get<string>(`${this.url}/logout`);
		
	}
}
