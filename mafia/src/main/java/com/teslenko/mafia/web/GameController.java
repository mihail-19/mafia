package com.teslenko.mafia.web;

import java.security.Principal;
import java.util.List;

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

import com.teslenko.mafia.dto.GameDto;
import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.GameCreateParams;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.UnauthorizedPlayerException;
import com.teslenko.mafia.services.GameService;
import com.teslenko.mafia.services.PlayerService;

@RestController
@RequestMapping("/game")
public class GameController {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);
	@Autowired
	private GameService gameService;
	@Autowired
	private PlayerService playerService;
	@Autowired 
	private SimpMessagingTemplate messagingTemplate;
	
	@PostMapping("/create")
	public Game createGame(@RequestParam(required = false) Integer dayTimeSeconds,
			@RequestParam(required = false) Integer nightTimeSeconds, 
			@RequestParam(required = false) Integer voteTimeSeconds, 
			@RequestParam(required = false) Integer mafiaNum, Principal principal) {
		String name = principal.getName();
		GameCreateParams gameCreateParams = GameCreateParams.builder()
				.dayTimeSeconds(dayTimeSeconds)
				.nightTimeSeconds(nightTimeSeconds)
				.voteTimeSeconds(voteTimeSeconds)
				.mafiaNum(mafiaNum)
				.build();
		LOGGER.info("creating game for player {}, mafiaNum {}, day time {}, night time {}", name, mafiaNum, dayTimeSeconds, nightTimeSeconds);
		Game res = gameService.addGame(gameCreateParams, playerService.getPlayer(name));
		return res;
	}
	@GetMapping("/{id}")
	public GameDto getGame(Principal user, @PathVariable int id) {
		LOGGER.info("getting game with id={} for user={}", id, user.getName() );
		Player receiver = playerService.getPlayer(user.getName());
		Game game = gameService.getGame(id);
		return new GameDto(game, receiver);
	}
	@GetMapping("/accessible-games-list")
	public List<Game> getAccessibleGames(){
		LOGGER.info("getting all accessible games");
		List<Game> games = gameService.getAccessibleGames();
		LOGGER.info("all accessible games {}", games);
		return games;
	}
	@GetMapping("/{id}/join")
	public Game joinGame(@PathVariable int id, Principal principal) {
		String name = principal.getName();
		LOGGER.info("joining game for player name=" + name + ", game with id = " + id);
		Player player = playerService.getPlayer(name);
		Game game = gameService.getGame(id);
		gameService.addPlayer(id, player);
		sendGameToPlayers(game);
		return game;
	}
	
	@GetMapping("/{id}/exit")
	public void extGame(@PathVariable int id, Principal principal) {
		String name = principal.getName();
		LOGGER.info("exit game id={} player={}", id, name);
		Player player = playerService.getPlayer(name);
		gameService.removePlayer(id, player);
	}
	/**
	 * Starts game. Only for game creator.
	 * @param name
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/start")
	public void gameStart(@PathVariable int id, Principal principal) {
		LOGGER.info("starting game id={}", id);
		Player initiator = playerService.getPlayer(principal.getName());
		Game game = gameService.startGame(initiator, id);
		sendGameToPlayers(game);
	}
	
	/**
	 * Stop game. Only for game creator.
	 * @param name
	 * @param id
	 */
	@GetMapping("/{id}/stop")
	public void stopGame(@PathVariable int id, Principal principal) {
		String name = principal.getName();
		LOGGER.info("stopping game with id={}", id);
		Player initiator = playerService.getPlayer(name);
		gameService.stopGame(initiator, id);
	}
	@PostMapping("/{id}/add-message")
	public Game addMessage(@PathVariable int id, @RequestParam String msg, Principal principal) {
		String name = principal.getName();
		LOGGER.info("adding message to chat game with id={}, player with name={}, message={}", id, name, msg);
		Player player = playerService.getPlayer(name);
		Game game = gameService.addMessage(id, player, msg);
		return game;
	}
	
	@PostMapping("/{id}/vote-citizen")
	public void voteCitizen(@RequestParam String target, @PathVariable int id, Principal principal) {
		String voterName = principal.getName();
		LOGGER.info("voting citizen game with id={}, voter={}, target={},", id, voterName, target);
		gameService.voteCitizen(id, playerService.getPlayer(voterName), playerService.getPlayer(target));
	}
	@PostMapping("/{id}/vote-mafia")
	public void voteMafia(@RequestParam String target, @PathVariable int id, Principal principal) {
		String voterName = principal.getName();
		LOGGER.info("voting mafia game with id={}, voter={}, target={},", id, voterName, target);
		gameService.voteMafia(id, playerService.getPlayer(voterName), playerService.getPlayer(target));
	}
	private void sendGameToPlayers(Game game) {
		game.sendGame();
	}
}
