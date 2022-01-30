package com.teslenko.mafia.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Chat {
	public static final int MAX_LINES = 50;
	private int gameId;
	Deque<Message> messages = new LinkedList<>();
	public Chat(int gameId) {
		this.gameId = gameId;
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
