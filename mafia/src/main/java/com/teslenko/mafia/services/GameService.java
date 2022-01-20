package com.teslenko.mafia.services;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.Player;

public interface GameService {
	int addGame(int dayTimeSeconds, int nightTimeSeconds, Player creator, int mafiaNum);
	Game getGame(int id);
	void startGame(int id);
	void stopGame(int id);
	void addPlayer(int id, Player player);
}
