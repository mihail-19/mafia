package com.teslenko.mafia.services;

import com.teslenko.mafia.entity.Player;

public interface PlayerService {
	Player createPlayer(String name);
	Player getPlayer(String name);
	Player getPlayer(int id);
	void remove(int id);
	boolean isFreeName(String name);
}
