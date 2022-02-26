package com.teslenko.mafia.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.NameBusyException;
import com.teslenko.mafia.exception.ValidationException;

/**
 * Service for mafia players. Stores data in {@link List}. Name should be
 * unique. Also player has unique ID.
 * 
 * @author Mykhailo Teslenko
 *
 */
@Service
public class PlayerServiceImpl implements PlayerService, UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerServiceImpl.class);

	@Value("${player.inspiration.minutes}")
	private int playerInspirationMinutes;

	@Autowired
	private Validator validator;

	@Autowired
	private HttpSession session;
	public static final String SESSION_PARAM_PLAYER_NAME = "name";
	private List<Player> players = new ArrayList<>();
	private volatile int playersMaxId = 1;

	@Override
	public synchronized Player createPlayer(String name) {
		if (!isFreeName(name)) {
			throw new NameBusyException("Name {" + name + "} already taken");
		}
		if (!validator.isValidPlayerName(name)) {
			throw new ValidationException("Name {" + name + "} is not valid");
		}
		Player player = new Player(name);
		player.setId(playersMaxId++);
		players.add(player);
		session.setAttribute(SESSION_PARAM_PLAYER_NAME, player.getName());
		return player;
	}

	@Override
	public Player getPlayer(String name) {
		return players.stream().filter((o) -> name.equals(o.getName())).findFirst().orElseThrow();
	}

	@Override
	public Player getPlayer(int id) {
		return players.stream().filter((o) -> id == o.getId()).findFirst().orElseThrow();
	}

	@Override
	public void remove(int id) {
		players.remove(getPlayer(id));
	}

	@Override
	public boolean isFreeName(String name) {
		return players.stream().noneMatch((o) -> name.equals(o.getName()));
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOGGER.trace("loadUserByName={}", username);
		createPlayer(username);
		getPlayer(username).resetAcitvity();
		List<GrantedAuthority> auths = new ArrayList<>();
		auths.add(new SimpleGrantedAuthority("USER"));
		User user = new User(username, "", auths);
		return user;
	}

	@Override
	public List<Player> getEnspiredPlayers() {
		return players.stream().filter((o) -> o.isInspired(playerInspirationMinutes)).collect(Collectors.toList());
	}

	@Override
	public List<Player> getAll() {
		return players;
	}

}
