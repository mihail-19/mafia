package com.teslenko.mafia.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.NameBusyException;
import com.teslenko.mafia.exception.ValidationException;

/**
 * Service for mafia players.
 * Stores data in {@link List}. Name should be unique.
 * Also player has unique ID.
 * 
 * @author Mykhailo Teslenko
 *
 */
@Service
public class PlayerServiceImpl implements PlayerService {
	
	@Autowired
	private Validator validator;
	
	private List<Player> players = new ArrayList<>();
	private volatile int playersMaxId = 1;
	
	@Override
	public synchronized Player createPlayer(String name) {
		if(!isFreeName(name)) {
			throw new NameBusyException("Name {"+ name + "} already taken");
		}
		if(!validator.isValidPlayerName(name)) {
			throw new ValidationException("Name {" + name  + "} is not valid");
		}
		Player player = new Player(name);
		player.setId(playersMaxId++);
		players.add(player);
		return player;
	}

	@Override
	public Player getPlayer(String name) {
		return players.stream().filter((o) -> name.equals(o.getName()))
				.findFirst()
				.orElseThrow();
	}

	@Override
	public Player getPlayer(int id) {
		return players.stream().filter((o) -> id == o.getId())
				.findFirst()
				.orElseThrow();
	}

	@Override
	public void remove(int id) {
		players.remove(getPlayer(id));
	}

	@Override
	public boolean isFreeName(String name) {
		return players.stream().noneMatch((o) -> name.equals(o.getName()));
	}

}
