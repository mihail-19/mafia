package com.teslenko.mafia.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.Player;

@Service
public class GameServiceImpl implements GameService{
	private List<Game> games = new ArrayList<>();
	private volatile int maxId = 1;
	@Override
	public synchronized int addGame(int dayTimeSeconds, int nightTimeSeconds, Player creator, int mafiaNum) {
		Game game = new Game(maxId++, dayTimeSeconds, nightTimeSeconds, creator, mafiaNum);
		return game.getId();
	}

	@Override
	public Game getGame(int id) {
		return games.parallelStream().filter((o) -> o.getId() == id).findFirst().orElseThrow();
	}

	@Override
	public void startGame(int id) {
		Game game = getGame(id);
		game.startGame();
	}

	@Override
	public void stopGame(int id) {
		Game game = getGame(id);
		game.stopGame();
	}

	@Override
	public void addPlayer(int id, Player player) {
		Game game = getGame(id);
		game.addPlayer(player);
	}
	
}
