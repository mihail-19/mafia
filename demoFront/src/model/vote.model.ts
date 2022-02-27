import {Player} from './player.model';
import {VotePlayers} from './votePlayers.model';
export class Vote {
	totalPlayers = 0;
	isStarted = false;
	isFinished = false;
	timeStart = '';
	voteTimeSeconds = 0;
	voteMap: VotePlayers[];
	constructor() {
		this.voteMap = [];
	}
	public getTargetForVoter(voter: Player): Player{
		console.log('get target for voter');
		var vp =  this.voteMap.find(o => o.voter.name == voter.name);
		if(vp === undefined){
			return new Player();
		}
		return vp.target;
	}
}
