import { Injectable } from '@angular/core';
import {HttpClient,HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError} from 'rxjs';
import { catchError} from 'rxjs/operators';
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
		return this.http.post<string>(`${this.url}/login-process`, params, { responseType:  'text' as 'json'})
		.pipe(catchError(this.handleError));
	}
	getName():Observable<string>{
		console.log('get name');
		return this.http.get<string>(`${this.url}/player-name`, { responseType:  'text' as 'json'});
	}
	
	logout(): Observable<string>{
		console.log('logout service');
		
		return this.http.get<string>(`${this.url}/logout`);
	}
	handleError(error: HttpErrorResponse) {
		if (error.status === 0) {
			// A client-side or network error occurred. Handle it accordingly.
			console.error('An error occurred:', error.error);
		} else {
			// The backend returned an unsuccessful response code.
			// The response body may contain clues as to what went wrong.
			console.error(
				`Backend returned code ${error.status}, body was: `, error.error);
		}
		// Return an observable with a user-facing error message.
		return throwError(error.error);
	}
}
