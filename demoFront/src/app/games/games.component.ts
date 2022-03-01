import { Component, OnInit, Inject } from '@angular/core';
import {GameService} from '../../services/game.service';
import {Game} from '../../model/game.model';
import { MatDialog, MatDialogRef, MatDialogConfig, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {AppComponent} from '../app.component'; 
@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.css']
})
export class GamesComponent implements OnInit {
	games: Game [] = [];
  constructor(private gameService: GameService, @Inject(MAT_DIALOG_DATA) public data: {appComponent: AppComponent}) {
	this.getAccessibleGames();
 }

  ngOnInit(): void {
  
	}
	
	getAccessibleGames(){
		this.gameService.getAccessibleGames().subscribe((o) => {
			this.games = o;
		});
	}
	joinGame(id: number){
		this.data.appComponent.gameId = id;
		this.data.appComponent.joinGame();
	}
}
