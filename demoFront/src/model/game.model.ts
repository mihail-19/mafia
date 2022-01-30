import {Player} from './player.model';
import {Vote} from './vote.model';
import {Chat} from './chat.model';

export class Game {
	id = -1;
	players:  Player[] | undefined; 
	creator: Player | undefined;
	dayTimeSeconds = 0;
	nightTimeSeconds = 0;
	isNight = false;
	isStarted = false;
	isFinished = false;
	mafiaNum = 0;
	startTime: string | undefined;
	date = new Date();
	chat = new Chat();
	vote = new Vote();
	constructor() {
	}
}
