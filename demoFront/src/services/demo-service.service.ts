import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class DemoServiceService {
	private url = "http://localhost:8083/session";
  constructor(private http:HttpClient) { }
	sendReq(): Observable<number>{
		return this.http.get<number>(this.url);
	}
}
