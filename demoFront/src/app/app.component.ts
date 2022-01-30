import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { GameService } from '../services/game.service';
import { FormsModule } from '@angular/forms';
import {Game} from '../model/game.model';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { timer, Subscription, Subject, interval } from 'rxjs';
@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {
	playersMaxNum = 10;
	title = 'demoFront';
	test = -1;
	name = "";
	mafiaNum = 0;
	gameId = -1;
	isGameCreated = true;
	isAuthorized: boolean;
	isGameStarted = false;
	game: Game | undefined;
	message = '';
	timerSubs: Subscription | undefined;
	timerValue = '';
	//stompClient = Stomp.over(new SockJS('http://localhost:8083/chat'));
	constructor(private authService: AuthService, private gameService: GameService) {
		
		let name = localStorage.getItem('name');
		let gameId = localStorage.getItem('gameId');
		if (!name) {
			this.isAuthorized = false;
		} else {
			this.isAuthorized = true;
			this.name = name;
		}
		if(gameId){
			this.gameId = +gameId;
			this.gameService.getGame(this.gameId).subscribe((o) => {
				this.game = o;
				this.connectWebSocket();
				this.setTimer();
			});
		}
	}

	login() {
		this.authService.login(this.name).subscribe((o) =>{
			 localStorage.setItem('name', o);
			this.isAuthorized = true;
			this.name = o;
		});
	}
	logout() {
		console.log('logout app');
		this.authService.logout().subscribe((o) => {
			this.isAuthorized = false;
			this.gameId = -1;
			this.isGameCreated = false;
			this.isGameStarted = false;
			this.game = undefined;
			localStorage.removeItem('gameId');
		});
	}
	createGame(){
		this.gameService.createGame(this.mafiaNum).subscribe((o) => {
			this.game = o;
			this.gameId = this.game.id;
			localStorage.setItem('gameId', this.gameId.toString());
			this.isGameCreated = true;
			this.connectWebSocket();
		});
	}
	startGame(){
		this.gameService.startGame(this.game!.id).subscribe((o) => {
			this.game = o;
			this.setTimer();
		});
	}
	joinGame(){
		this.gameService.joinGame(this.gameId).subscribe((o) => {
			this.game = o;
			this.isGameCreated = true;
			localStorage.setItem('gameId', this.gameId.toString());
			this.connectWebSocket();
		});
	}
	stopGame(){
		this.gameService.stopGame(this.game!.id).subscribe((o) => {
			this.game = undefined;
			localStorage.removeItem('gameId');
			this.gameId = -1;
			//this.stompClient = Stomp.over(new SockJS('http://localhost:8083/chat'));
		})
	}
	getGame(){
		this.gameService.getGame(this.gameId).subscribe((o) => {
			this.game = o;
			this.setTimer();
			console.log('get game');
		});
	}
	sendMsg(){
		this.gameService.sendMessage(this.game!.id, this.message).subscribe((o) =>{
			this.game = o;
			this.message = '';
		});
	}
	connectWebSocket(){
		let ws = new SockJS('http://localhost:8083/chat');
		let stompClient = Stomp.over(ws);
		console.log('conectWebSocket');
		let that = this;
		stompClient.connect({}, function(frame){
			stompClient.subscribe(`/chat/${that.game!.id}`, (message) =>{
				that.game = JSON.parse(message.body);
				that.setTimer();
			});
		});
	}
	setTimer(){
		
		this.timerSubs?.unsubscribe();
		this.timerSubs = timer(0, 1000).subscribe((o) =>{
			if(this.game && this.game.isStarted){
				var split = this.game.startTime!.split(":");
				var date = new Date();
				
				var start = parseInt(split[0])*3600 + parseInt(split[1])*60 + parseInt(split[2]);
				var current = date.getHours()*3600 + date.getMinutes()*60 + date.getSeconds();
				var del;
				if(this.game.isNight){
					del = this.game.nightTimeSeconds;
				} else{
					del = this.game.dayTimeSeconds;
				}
				var snds = del - (current - start);
				this.timerValue = `${Math.floor(snds/60)} : ${snds%60}`;
			}
		});
	}
	voteCitizen(name: string){
		this.gameService.voteCitizen(this.game!.id, name).subscribe();
	}
}
