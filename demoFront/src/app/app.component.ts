import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {
	title = 'demoFront';
	test = -1;
	name = "";
	mafiaNum = 0;
	gameId = -1;
	isAuthorized: boolean;
	isGameStarted = false;
	constructor(private authService: AuthService) {
		if (localStorage.getItem('name') == null) {
			this.isAuthorized = false;
		} else {
			this.isAuthorized = true;
		}
	}

	login() {
		console.log(this.name);
		this.authService.login(this.name).subscribe((o) =>{
			 localStorage.setItem('name', o);
			this.isAuthorized = true;
		});
	}
	logout() {
		this.authService.logout().subscribe((o) => {
			localStorage.removeItem('name');
			this.isAuthorized = false;
		});
	}
	
	startGame(){
		
	}
	joinGame(){
		
	}
}
