import {Player} from './player.model';
import {VotePlayers} from './votePlayers.model';
export class Vote {
	totalPlayers = 0;
	isStarted = false;
	timeStart = '';
	voteTimeSeconds = 0;
	voteMap: VotePlayers[];
	constructor() {
		this.voteMap = [];
	}

}
