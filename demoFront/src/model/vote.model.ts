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
	getTargetForVoter(voter: Player): Player{
		var vp =  this.voteMap.find(o => o.voter == voter);
		if(vp === undefined){
			return new Player();
		}
		return vp.target;
	}
}
