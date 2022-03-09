package com.teslenko.mafia.services;

import java.util.List;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.GameCreateParams;
import com.teslenko.mafia.entity.Player;

public interface GameService {
	List<Game> getAll();
	Game addGame(GameCreateParams gameCreatePrams, Player creator);
	Game getGame(int id);
	Game startGame(Player initiator, int id);
	void stopGame(Player initiator, int id);
	void addPlayer(int id, Player player);
	Game addMessage(int id, Player player, String msg);
	void voteCitizen(int id, Player voter, Player target);
	void voteMafia(int id, Player voter, Player target);
	List<Game> getAccessibleGames();
	void removePlayer(int id, Player player);
}
