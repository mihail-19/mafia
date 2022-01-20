package com.teslenko.mafia.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.UnauthorizedPlayerException;
import com.teslenko.mafia.services.GameService;

@RestController
@RequestMapping("/game")
@SessionAttributes("player")
public class GameController {
	@Autowired
	private GameService gameService;
	
	@PostMapping("/create")
	public Integer createGame(@ModelAttribute("player") Player player,
			@RequestParam int mafiaNum) {
		if(player == null) {
			throw new UnauthorizedPlayerException("not registered");
		}
		int gameId = gameService.addGame(60, 30, player, mafiaNum);
		return gameId;
	}
	@GetMapping("/{id}")
	public Game getGame(@PathVariable int id) {
		Game game = gameService.getGame(id);
		
		return game;
	}
	@GetMapping("/{id}/join")
	public Game joinGame(@ModelAttribute("player") Player player, @PathVariable int id) {
		Game game = gameService.getGame(id);
		if(player == null) {
			throw new UnauthorizedPlayerException("not registered");
		}
		gameService.addPlayer(id, player);
		return game;
	}
	
	@GetMapping("/{id}/vote-start")
	public void voteStart(@PathVariable int id) {
		
	}
	
	@PostMapping("/{id}/vote-citizen")
	public void voteCitizen(@RequestParam int playerId) {
		
	}
}
