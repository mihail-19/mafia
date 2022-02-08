package com.teslenko.mafia.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.UnauthorizedPlayerException;
import com.teslenko.mafia.services.GameService;
import com.teslenko.mafia.services.PlayerService;

@RestController
@RequestMapping("/game")
public class GameController {
	public static final int DAY_TIME = 10;
	public static final int NIGHT_TIME = 10;
	private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);
	@Autowired
	private GameService gameService;
	@Autowired
	private PlayerService playerService;
	@Autowired 
	private SimpMessagingTemplate messagingTemplate;
	
	@PostMapping("/create")
	public Game createGame(@RequestHeader("name") String name,
			@RequestParam int mafiaNum) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to create game, player with name={} is not registered", name);
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		LOGGER.info("creating game for player {" + name + "}, mafia num = " + mafiaNum);
		Game res = gameService.addGame(DAY_TIME, NIGHT_TIME, playerService.getPlayer(name), mafiaNum);
		return res;
	}
	@GetMapping("/{id}")
	public Game getGame(Principal user, @PathVariable int id) {
		LOGGER.info("getting game with id={} for user={}", id, user.getName() );
		Game game = gameService.getGame(id);
		
		return game;
	}
	@GetMapping("/{id}/join")
	public Game joinGame(@RequestHeader("name") String name, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to join game with id={}, player with name={} is not registered", id, name);
			throw new UnauthorizedPlayerException("not registered player with name=" + name);
		}
		LOGGER.info("joining game for player name=" + name + ", game with id = " + id);
		Player player = playerService.getPlayer(name);
		Game game = gameService.getGame(id);
		gameService.addPlayer(id, player);
		sendGameToPlayers(game);
		return game;
	}
	
	/**
	 * Starts game. Only for game creator.
	 * @param name
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/start")
	public Game gameStart(@RequestHeader("name") String name, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to start game with id={}, player with name={} is not registered",id, name);
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		LOGGER.info("starting game id={}", id);
		Player initiator = playerService.getPlayer(name);
		Game game = gameService.startGame(initiator, id);
		sendGameToPlayers(game);
		return game;
	}
	
	/**
	 * Stop game. Only for game creator.
	 * @param name
	 * @param id
	 */
	@GetMapping("/{id}/stop")
	public void stopGame(@RequestHeader("name") String name, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to stop game with id={}, player with name={} is not registered", id, name);
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		LOGGER.info("stopping game with id={}", id);
		Player initiator = playerService.getPlayer(name);
		Game game = gameService.getGame(id);
		gameService.stopGame(initiator, id);
		game.setIsFinished(true);
		sendGameToPlayers(game);
		
	}
	@PostMapping("/{id}/add-message")
	public Game addMessage(@RequestHeader("name") String name, @PathVariable int id, @RequestParam String msg) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to add message to chat game with id={}, player with name={} is not registered", id, name);
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		LOGGER.info("adding message to chat game with id={}, player with name={}, message={}", id, name, msg);
		Player player = playerService.getPlayer(name);
		Game game = gameService.addMessage(id, player, msg);
		
		return game;
	}
	
	@PostMapping("/{id}/vote-citizen")
	public void voteCitizen(@RequestHeader("name") String name, @RequestParam String target, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to vote citizen game with id={}, player with name={} is not registered", id, name);
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		LOGGER.info("voting citizen game with id={}, voter={}, target={},", id, name, target);
		gameService.voteCitizen(id, playerService.getPlayer(name), playerService.getPlayer(target));
	}
	@PostMapping("/{id}/vote-mafia")
	public void voteMafia(@RequestHeader("name") String name, @RequestParam String target, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			LOGGER.warn("failed to vote mafia game with id={}, player with name={} is not registered", id, name);
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		LOGGER.info("voting mafia game with id={}, voter={}, target={},", id, name, target);
		gameService.voteMafia(id, playerService.getPlayer(name), playerService.getPlayer(target));
	}
	private void sendGameToPlayers(Game game) {
		messagingTemplate.convertAndSend("/chat/" + game.getId(), game);
	}
}
