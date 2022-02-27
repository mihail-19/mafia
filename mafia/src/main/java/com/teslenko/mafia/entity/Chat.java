package com.teslenko.mafia.entity;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

public class Chat {
	public static final int MAX_LINES = 50;
	Deque<Message> messages = new LinkedList<>();
	public Chat() {
	}
	
	/**
	 * Prepares chat with greeting message. It will automatically became a first message.
	 */
	public void setGreetingMsg(String msg) {
		if(messages.size() < MAX_LINES) {
			Message greetings = new Message("~ADMIN~", msg);
			messages.addFirst(greetings);
		}
	}
	
	/**
	 * Adds a message into collection.
	 * If messages number is bigger than limit,
	 * removes first message before.
	 * @return
	 */
	public void addMessage(Message msg) {
		if(messages.size() < MAX_LINES) {
			messages.add(msg);
		} else {
			messages.pollFirst();
			messages.add(msg);
		}
	}
	public Collection<Message> getMessages(){
		return messages;
	}
	@Override
	public String toString() {
		StringBuilder sb  = new StringBuilder();
		for(Message msg : messages) {
			sb.append("[")
			.append(msg.getAuthorName())
			.append("]: ")
			.append(msg.getMsg())
			.append(System.lineSeparator());
		}
		return sb.toString().trim();
	}
}
