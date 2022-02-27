import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { GameService } from '../services/game.service';
import { FormsModule } from '@angular/forms';
import { Game } from '../model/game.model';
import { Player } from '../model/player.model';
import { VotePlayers } from '../model/votePlayers.model';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { timer, Subscription, Subject, interval } from 'rxjs';
@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {
	myRole = '';
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
	timerValue = '00:00';
	timerVoteSubs: Subscription | undefined;
	timerVoteValue = '00:00';
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
		if (gameId) {
			this.gameId = +gameId;
			this.gameService.getGame(this.gameId).subscribe((o) => {
				this.game = o;
				this.connectWebSocket();
				this.setTimer();
			});
		}
	}

	login() {
		this.authService.login(this.name).subscribe((o: string) => {
			this.isAuthorized = true;
			this.authService.getName().subscribe((n) => {
				localStorage.setItem('name', n);
				this.name = n;
			});
		},
		(error) =>{
				console.error(`error while login ${error}`);
		});
	}
	logout() {
		console.log('logout app');
		localStorage.removeItem('name');
		this.isAuthorized = false;
		this.authService.logout().subscribe((o) => {
			this.isAuthorized = false;
			this.gameId = -1;
			this.isGameCreated = false;
			this.isGameStarted = false;
			this.game = undefined;

			localStorage.removeItem('gameId');
		});
	}
	createGame() {
		this.gameService.createGame(this.mafiaNum).subscribe((o) => {
			this.game = o;
			this.gameId = this.game.id;
			localStorage.setItem('gameId', this.gameId.toString());
			this.isGameCreated = true;
			this.connectWebSocket();
		});
	}
	startGame() {
		this.gameService.startGame(this.game!.id).subscribe();
	}
	joinGame() {
		this.gameService.joinGame(this.gameId).subscribe((o) => {
			this.game = o;
			this.isGameCreated = true;
			localStorage.setItem('gameId', this.gameId.toString());
			this.connectWebSocket();
		});
	}
	stopGame() {
		this.gameService.stopGame(this.game!.id).subscribe((o) => {
			this.game = undefined;
			localStorage.removeItem('gameId');
			this.gameId = -1;
			//this.stompClient = Stomp.over(new SockJS('http://localhost:8083/chat'));
		})
	}
	getGame() {
		this.gameService.getGame(this.gameId).subscribe((o) => {
			this.game = o;
			this.setTimer();
			console.log('get game');
		});
	}
	sendMsg() {
		this.gameService.sendMessage(this.game!.id, this.message).subscribe((o) =>{
			this.message = '';
		});
	}
	//Connection via tcp/ip to know when to refresh game.
	//Refresh through http to determine user name and role to restrict info about other users
	connectWebSocket() {
		let ws = new SockJS('http://localhost:8083/chat');
		let stompClient = Stomp.over(ws);
		console.log('conectWebSocket');
		let that = this;
		stompClient.connect({}, function(frame) {
			stompClient.subscribe(`/chat/${that.game!.id}`, (message) => {
				
				//message contains no useful info, it is just a marker to refresh game.
				
				//that.game = JSON.parse(message.body);
				//that.setTimer();
				that.getGame();
			});
		});
	}
	setTimer() {
		this.timerSubs?.unsubscribe();
		this.timerVoteSubs?.unsubscribe();
		if (this.game && this.game.isStarted) {
			if (!this.game.vote || !this.game.vote.isStarted || this.game.vote.isFinished) {
				this.setTimerNormal();
			} else {
				this.setTimerVote();
			}
		}
	}
	setTimerNormal() {
		this.timerSubs = timer(0, 1000).subscribe((o) => {
			if (this.game && this.game.isStarted) {
				var split = this.game.startTime!.split(":");
				var date = new Date();

				var start = parseInt(split[0]) * 3600 + parseInt(split[1]) * 60 + parseInt(split[2]);
				var current = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();
				var del;
				if (this.game.isNight) {
					del = this.game.nightTimeSeconds;
				} else {
					del = this.game.dayTimeSeconds;
				}
				var snds = del - (current - start);
				var minutes = `${Math.floor(snds / 60)}`;
				if(minutes.length < 2){
					minutes = `0${minutes}`;
				}
				var seconds = `${snds % 60}`;
				if(seconds.length < 2){
					seconds = `0${seconds}`;
				}
				this.timerValue = `${minutes}:${seconds}`;
			}
		});

	}
	setTimerVote() {
		this.timerVoteSubs = timer(0, 1000).subscribe((o) => {
			if (this.game && this.game.isStarted) {
				var split = this.game.vote.timeStart.split(":");
				var date = new Date();
				var start = parseInt(split[0]) * 3600 + parseInt(split[1]) * 60 + parseInt(split[2]);
				var current = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();
				var del = this.game.vote.voteTimeSeconds;
				var snds = del - (current - start);
				var minutes = `${Math.floor(snds / 60)}`;
				if(minutes.length < 2){
					minutes = `0${minutes}`;
				}
				var seconds = `${snds % 60}`;
				if(seconds.length < 2){
					seconds = `0${seconds}`;
				}
				this.timerVoteValue = `${minutes}:${seconds}`;
			}
		});
	}
	voteCitizen(name: string) {
		this.gameService.voteCitizen(this.game!.id, name).subscribe();
	}
	voteMafia(name: string){
		this.gameService.voteMafia(this.game!.id, name).subscribe();
	}
	getTargetNameForVoter(voter: Player, voteMap: VotePlayers[]): string {
		var vp = voteMap.find(o => o.voter.name == voter.name);
		if (vp === undefined) {
			return '';
		}
		return vp.target.name;
	}
	
	getMyRole(): string{
		if(!this.game || !this.game.isStarted){
			return '';
		}
		var p = this.game.players?.find(o => o.name == this.name);
		if(p === undefined){
			return '';
		}
		return p.roleType;
	}
}
