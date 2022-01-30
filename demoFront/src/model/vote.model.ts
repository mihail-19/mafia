import {Player} from './player.model';
export class Vote {
	totalPlayers = 0;
	isStarted = false;
	timeStart = '';
	voteTimeSeconds = 0;
	voteMap =  new Map<Player, Player>();
	constructor() {
	}

}
