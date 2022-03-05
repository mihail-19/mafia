import {Injectable, isDevMode} from '@angular/core';
@Injectable({
  providedIn: 'root'
})
export class ServerUrl{
	serverUrl = '';
	constructor(){
		if(isDevMode()){
			this.serverUrl = "http://localhost:8083"
		} else {
			this.serverUrl = "http://178.151.21.70:8083"
		}
	}
}