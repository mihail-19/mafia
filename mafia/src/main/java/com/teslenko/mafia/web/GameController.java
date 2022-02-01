package com.teslenko.mafia.web;

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
		LOGGER.trace("create game for player {" + name + "}, mafia num = " + mafiaNum);
		if(playerService.isFreeName(name)) {
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		Game res = gameService.addGame(DAY_TIME, NIGHT_TIME, playerService.getPlayer(name), mafiaNum);
		return res;
	}
	@GetMapping("/{id}")
	public Game getGame(@PathVariable int id) {
		Game game = gameService.getGame(id);
		
		return game;
	}
	@GetMapping("/{id}/join")
	public Game joinGame(@RequestHeader("name") String name, @PathVariable int id) {
		LOGGER.trace("join game for player {" + name + "}, game id = " + id);
		if(playerService.isFreeName(name)) {
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
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
	public Game voteStart(@RequestHeader("name") String name, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
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
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		Player initiator = playerService.getPlayer(name);
		Game game = gameService.getGame(id);
		gameService.stopGame(initiator, id);
		game.setIsFinished(true);
		sendGameToPlayers(game);
		
	}
	@PostMapping("/{id}/add-message")
	public Game addMessage(@RequestHeader("name") String name, @PathVariable int id, @RequestParam String msg) {
		if(playerService.isFreeName(name)) {
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		Player player = playerService.getPlayer(name);
		
		Game game = gameService.addMessage(id, player, msg);
		sendGameToPlayers(game);
		return game;
	}
	
	@PostMapping("/{id}/vote-citizen")
	public void voteCitizen(@RequestHeader("name") String name, @RequestParam String target, @PathVariable int id) {
		if(playerService.isFreeName(name)) {
			throw new UnauthorizedPlayerException("not registered player with name {" + name + "}");
		}
		gameService.voteCitizen(id, playerService.getPlayer(name), playerService.getPlayer(target));
	}
	private void sendGameToPlayers(Game game) {
		messagingTemplate.convertAndSend("/chat/" + game.getId(), game);
	}
}
