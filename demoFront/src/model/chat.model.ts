import { Injectable } from '@angular/core';
import {Message} from './message.model';
export class Chat {
	messages: Message[]; 
	constructor() {
		this.messages = [];
	}

}
