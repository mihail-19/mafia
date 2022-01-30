package com.teslenko.mafia.services;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.Player;

public interface GameService {
	Game addGame(int dayTimeSeconds, int nightTimeSeconds, Player creator, int mafiaNum);
	Game getGame(int id);
	Game startGame(Player initiator, int id);
	void stopGame(Player initiator, int id);
	void addPlayer(int id, Player player);
	Game addMessage(int id, Player player, String msg);
	void voteCitizen(int id, Player voter, Player target);
}
