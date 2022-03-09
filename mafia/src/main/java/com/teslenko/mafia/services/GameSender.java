package com.teslenko.mafia.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.teslenko.mafia.entity.Game;

/**
 * Game communicator with players. Sends requests to all players connected to the game.
 * Should be attached to any game.
 * @author Mykhailo Teslenko
 *
 */
public class GameSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameSender.class);
	private final String msgUrl;
	private SimpMessagingTemplate messagingTemplate;
	private Game game;
	public GameSender(SimpMessagingTemplate messagingTemplate, Game game) {
		this.messagingTemplate = messagingTemplate;
		this.game = game;
		msgUrl = "/chat/" + game.getId();
	}
	
	public void sendGameToPlayers() {
		LOGGER.trace("sending game to players, game={}", game);
		messagingTemplate.convertAndSend(msgUrl, game);
	}
	
	public void sendRequestForGameRefresh() {
		LOGGER.trace("sending request for game refreshing to all players, game {}", game);
		messagingTemplate.convertAndSend(msgUrl, "refresh");
	}
	
	public void sendGameEnd() {
		LOGGER.info("sending game is finished message");
		messagingTemplate.convertAndSend(msgUrl, "end");
	}
}
