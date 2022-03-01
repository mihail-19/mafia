import { Injectable } from '@angular/core';
import {Message} from './message.model';
export class GameParams {
	mafiaNum = 1;
	dayTimeSeconds = 120;
	nightTimeSeconds = 60;
	voteTimeSeconds = 30;
	constructor() {
	}

}
