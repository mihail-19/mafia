package com.teslenko.mafia.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.Message;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.UnauthorizedPlayerException;
import com.teslenko.mafia.exception.VoteException;
import com.teslenko.mafia.web.GameController;

@Service
public class GameServiceImpl implements GameService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	private List<Game> games = new ArrayList<>();
	private volatile int maxId = 1;

	@Override
	public synchronized Game addGame(int dayTimeSeconds, int nightTimeSeconds, Player creator, int mafiaNum) {
		Game game = new Game(maxId++, dayTimeSeconds, nightTimeSeconds, creator, mafiaNum);
		games.add(game);
		game.addPlayer(creator);
		LOGGER.trace("Game is created, all games -> " + games);
		return game;
	}

	@Override
	public Game getGame(int id) {
		LOGGER.trace("Getting game id=" + id + " from all games -> " + games);
		return games.stream().filter((o) -> o.getId() == id).findFirst().orElseThrow();
	}

	@Override
	public Game startGame(Player initiator, int id) {
		LOGGER.trace("starting game {" + id + "}");
		Game game = getGame(id);
		if (game.getCreator().equals(initiator)) {
			game.startGame();
			turnToDay(game);
		} else {
			throw new UnauthorizedPlayerException(
					"Player {" + initiator.getName() + "} is not creator for game {" + id + "}");
		}
		return game;
	}

	@Override
	public void stopGame(Player initiator, int id) {
		Game game = getGame(id);
		if (!game.getCreator().equals(initiator)) {
			throw new UnauthorizedPlayerException(
					"Player {" + initiator.getName() + "} is not creator for game {" + id + "}");
		} else {
			List<Player> gamePlayers = game.getPlayers();
			for (Player p : gamePlayers) {
				p.setAlive(true);
				p.setMafiaRole(null);
				p.setRoleType(null);
				p.setUntouchable(false);
			}
			games.removeIf((o) -> o.getId() == id);
		}
	}

	@Override
	public void addPlayer(int id, Player player) {
		Game game = getGame(id);
		if (!game.hasPlayer(player)) {
			game.addPlayer(player);
		}

	}

	@Override
	public Game addMessage(int id, Player player, String msg) {
		Game game = getGame(id);
		if (game.hasPlayer(player)) {
			game.getChat().addMessage(new Message(player.getName(), msg));
		}
		return game;
	}
	
	/**
	 * Method for switching day and night in game. Uses another Thread.
	 * @param game
	 */
	public void turnToDay(Game game) {
		game.setIsNight(false);
		game.setStartTime(LocalTime.now());
		new Thread(() -> {
			while (!game.getIsFinished()) {
				LocalTime finishTime = game.finishTime();
				while (LocalTime.now().isBefore(finishTime)) {
					sleepWithPeriod(100);
				}
				if (!game.getIsNight()) {
					startVoteCitizen(game);
				}else {
					game.setIsNight(false);
					game.resetVoteCitizen();
				}
				game.setStartTime(LocalTime.now());
				sendGameToPlayers(game);
			}
		}).start();
	}
	private void startVoteCitizen(Game game) {
		LOGGER.trace("starting citizen vote for game " + game.getId());
		game.resetVoteCitizen();
		game.getVote().startVote();
		sendGameToPlayers(game);
		while(!game.getVote().isFinished()) {
			sleepWithPeriod(100);
		}
		LOGGER.trace("citizen vote for game " + game.getId() + " is finished -> " + game.getVote());
		game.killPlayerByVoteResults();
		game.setIsNight(true);
	}
	private void startVoteMafia(Game game) {
		LOGGER.trace("starting mafia vote for game " + game.getId());
		game.resetVoteMafia();
		game.getVote().startVote();
		sendGameToPlayers(game);
		while(!game.getVote().isFinished()) {
			sleepWithPeriod(100);
		}
		LOGGER.trace("mafia vote for game " + game.getId() + " is finished -> " + game.getVote());
		game.killPlayerByVoteResults();
		game.setIsNight(true);
	}
	private void sleepWithPeriod(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void voteCitizen(int id, Player voter, Player target) {
		Game game = getGame(id);
		if(!game.hasPlayer(voter)) {
			throw new UnauthorizedPlayerException(
					"Player {" + voter.getName() + "} is not registered in game {" + id + "}");
		}
		if(game.getIsNight()) {
			throw new VoteException("Citizens sleeps at night!");
		}
		game.citizenVoteAgain(voter, target);
		sendGameToPlayers(game);
	}
	private void sendGameToPlayers(Game game) {
		messagingTemplate.convertAndSend("/chat/" + game.getId(), game);
	}
}
