import { Injectable } from '@angular/core';
import {HttpClient,HttpParams, HttpHeaders} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import {Game} from '../model/game.model';
import {GameParams} from '../model/game.params.model';
import { catchError} from 'rxjs/operators';
@Injectable({
  providedIn: 'root'
})
export class GameService {
	private url = "http://localhost:8083/game";
  constructor(private http:HttpClient) { }

	createGame(gameParams: GameParams): Observable<Game>{
		let params: HttpParams = new HttpParams();
		params = params
						.set('mafiaNum', gameParams.mafiaNum)
						.set('dayTimeSeconds', gameParams.dayTimeSeconds)
						.set('nightTimeSeconds', gameParams.nightTimeSeconds)
						.set('voteTimeSeconds', gameParams.voteTimeSeconds);
		return this.http.post<Game>(`${this.url}/create`, params);
	}
	joinGame(id: number): Observable<Game>{
		let url = `${this.url}/${id}/join`;
		return this.http.get<Game>(url);
	}
	getGame(id: number): Observable<Game>{
		let url = `${this.url}/${id}`;
		return this.http.get<Game>(url).pipe(catchError((e) => throwError(e.error)));
	}
	getAccessibleGames(): Observable<Game []>{
		let url = `${this.url}/accessible-games-list`;
		return this.http.get<Game []>(url);
	}
	stopGame(id: number): Observable<any>{
		let url = `${this.url}/${id}/stop`;
		return this.http.get(url);
	}
	startGame(id: number): Observable<any>{
		let url = `${this.url}/${id}/start`;
		return this.http.get(url);
	}
	sendMessage(id: number, msg: string): Observable<any>{
		let url = `${this.url}/${id}/add-message`;
		let params: HttpParams = new HttpParams();
		params = params.set('msg', msg);
		return this.http.post(url, params);
	}
	voteCitizen(id: number, target: string): Observable<any>{
		let url = `${this.url}/${id}/vote-citizen`;
		let params: HttpParams = new HttpParams();
		params = params.set('target', target);
		return this.http.post(url, params);
	}
	voteMafia(id: number, target: string): Observable<any>{
		let url = `${this.url}/${id}/vote-mafia`;
		let params: HttpParams = new HttpParams();
		params = params.set('target', target);
		return this.http.post(url, params);
	}
	exitGame(id: number): Observable<any>{
		let url = `${this.url}/${id}/exit`;
		return this.http.get(url);
	}
	
}
