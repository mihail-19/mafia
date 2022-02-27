package com.teslenko.mafia.entity;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.teslenko.mafia.services.GameSender;

/**
 * Builds a new game with given id. MessagingTemplate should be added.
 * @author Mykhailo Teslenko
 *
 */
public class GameBuilder {
	private int id;
	private Player creator;
	private int dayTimeSeconds;
	private int nightTimeSeconds;
	private int mafiaNum;
	
	//For communication with plaayers
	private SimpMessagingTemplate messagingTemplate;
	
	public GameBuilder(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	public GameBuilder id(int id) {
		this.id = id;
		return this;
	}
	
	public GameBuilder creator(Player creator) {
		this.creator = creator;
		return this;
	}
	public GameBuilder dayTimeSeconds(int dayTimeSeconds) {
		this.dayTimeSeconds = dayTimeSeconds;
		return this;
	}
	public GameBuilder nightTimeSeconds(int nightTimeSeconds) {
		this.nightTimeSeconds = nightTimeSeconds;
		return this;
	}
	public GameBuilder mafiaNum(int mafiaNum) {
		this.mafiaNum = mafiaNum;
		return this;
	}
	public GameBuilder messagingTemplate(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
		return this;
	}
	public Game create() {
		Game game = new Game(id, dayTimeSeconds, nightTimeSeconds, creator, mafiaNum);
		game.setGameSender(new GameSender(messagingTemplate, game));
		return game;
	}
}
