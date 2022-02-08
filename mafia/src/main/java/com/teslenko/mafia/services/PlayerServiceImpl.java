package com.teslenko.mafia.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.PlayerUserDetails;
import com.teslenko.mafia.exception.NameBusyException;
import com.teslenko.mafia.exception.ValidationException;
import com.teslenko.mafia.web.GameController;

/**
 * Service for mafia players.
 * Stores data in {@link List}. Name should be unique.
 * Also player has unique ID.
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
		if(!isFreeName(name)) {
			throw new NameBusyException("Name {"+ name + "} already taken");
		}
		if(!validator.isValidPlayerName(name)) {
			throw new ValidationException("Name {" + name  + "} is not valid");
		}
		Player player = new Player(name);
		player.setId(playersMaxId++);
		players.add(player);
		session.setAttribute(SESSION_PARAM_PLAYER_NAME, player.getName());
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
	
	private boolean containsPlayerWithName(String name) {
		try {
			getPlayer(name);
			return true;
		} catch(NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOGGER.trace("loadUserByName={}", username);
		if(session.getAttribute(SESSION_PARAM_PLAYER_NAME) == null) {
			
			LOGGER.error("Name={} does not stored in session", username);
			throw new UsernameNotFoundException("Name=" + username + " does is not stored in session") ;
		} else {
			String name = session.getAttribute(SESSION_PARAM_PLAYER_NAME).toString();
			if(!containsPlayerWithName(name)) {
				LOGGER.error("Name={} is invalid", username);
				throw new UsernameNotFoundException("Name=" + username + " is invalid") ;
			}
			getPlayer(name).resetAcitvity();
			return new PlayerUserDetails(name);
		}
	}

	@Override
	public List<Player> getEnspiredPlayers() {
		return players.stream()
				.filter((o) -> o.isInspired(playerInspirationMinutes))
				.collect(Collectors.toList());
	}

}
