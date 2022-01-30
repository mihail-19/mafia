import { Injectable } from '@angular/core';
import {HttpClient,HttpParams, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Game} from '../model/game.model';
@Injectable({
  providedIn: 'root'
})
export class GameService {
	private url = "http://localhost:8083/game";
  constructor(private http:HttpClient) { }

	createGame(mafiaNum: number): Observable<Game>{
		var name = localStorage.getItem('name');
		let params: HttpParams = new HttpParams();
		params = params.set('mafiaNum', mafiaNum);
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.post<Game>(`${this.url}/create`, params, {'headers': headers});
		} else {
			return this.http.post<Game>(`${this.url}/logout`, params);
		}
	}
	joinGame(id: number): Observable<Game>{
		var name = localStorage.getItem('name');
		let url = `${this.url}/${id}/join`;
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.get<Game>(url, {'headers': headers});
		} else {
			return this.http.get<Game>(url);
		}
	}
	getGame(id: number): Observable<Game>{
		var name = localStorage.getItem('name');
		let url = `${this.url}/${id}`;
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.get<Game>(url, {'headers': headers});
		} else {
			return this.http.get<Game>(url);
		}
	}
	stopGame(id: number): Observable<any>{
		var name = localStorage.getItem('name');
		let url = `${this.url}/${id}/stop`;
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.get(url, {'headers': headers});
		} else {
			return this.http.get(url);
		}
	}
	startGame(id: number): Observable<Game>{
		var name = localStorage.getItem('name');
		let url = `${this.url}/${id}/start`;
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.get<Game>(url, {'headers': headers});
		} else {
			return this.http.get<Game>(url);
		}
	}
	sendMessage(id: number, msg: string): Observable<Game>{
		var name = localStorage.getItem('name');
		let url = `${this.url}/${id}/add-message`;
		let params: HttpParams = new HttpParams();
		params = params.set('msg', msg);
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.post<Game>(url, params, {'headers': headers});
		} else {
			return this.http.post<Game>(url, params);
		}
	}
	voteCitizen(id: number, target: string): Observable<any>{
		var name = localStorage.getItem('name');
		let url = `${this.url}/${id}/vote-citizen`;
		let params: HttpParams = new HttpParams();
		params = params.set('target', target);
		if(name){
			var headers = new HttpHeaders().set('name', name);
			return this.http.post<Game>(url, params,  {'headers': headers});
		} else {
			return this.http.post<Game>(url, params);
		}
	}
	
}
