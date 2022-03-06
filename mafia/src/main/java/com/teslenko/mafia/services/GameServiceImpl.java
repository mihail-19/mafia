package com.teslenko.mafia.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.GameBuilder;
import com.teslenko.mafia.entity.GameCreateParams;
import com.teslenko.mafia.entity.GameProcess;
import com.teslenko.mafia.entity.MafiaRoleSetter;
import com.teslenko.mafia.entity.Message;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.RoleType;
import com.teslenko.mafia.exception.IllegalMafiaRoleActionException;
import com.teslenko.mafia.exception.NameBusyException;
import com.teslenko.mafia.exception.UnauthorizedPlayerException;
import com.teslenko.mafia.exception.VoteException;
import com.teslenko.mafia.web.GameController;

@Service
public class GameServiceImpl implements GameService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired 
	private MafiaRoleSetter mafiaRoleSetter;
	
	private List<Game> games = new ArrayList<>();
	private volatile int maxId = 1;

	@Override
	public synchronized Game addGame(GameCreateParams gameCreateParams, Player creator) {
		Game game = new GameBuilder(messagingTemplate)
				.id(maxId)
				.dayTimeSeconds(gameCreateParams.getDayTimeSeconds())
				.nightTimeSeconds(gameCreateParams.getNightTimeSeconds())
				.creator(creator)
				.mafiaNum(gameCreateParams.getMafiaNum())
				.create();
		games.add(game);
		maxId++;
		LOGGER.info("Game is created game={}", game);
		return game;
	}

	@Override
	public Game getGame(int id) {
		LOGGER.info("Getting game id=" + id + " from all games -> " + games);
		return games.stream().filter((o) -> o.getId() == id).findFirst().orElseThrow(() -> {
			LOGGER.warn("no game found for id={}", id);
			return new NoSuchElementException("No game found for id=" + id);
		});
	}

	@Override
	public Game startGame(Player initiator, int id) {
		LOGGER.trace("starting game {" + id + "}");
		Game game = getGame(id);
		if (game.getCreator().equals(initiator)) {
			game.startGame(mafiaRoleSetter, new GameProcess(game));
		} else {
			throw new UnauthorizedPlayerException(
					"Player {" + initiator.getName() + "} is not creator for game {" + id + "}");
		}
		return game;
	}

	@Override
	public void stopGame(Player initiator, int id) {
		LOGGER.info("stopping game with id={}, by player={}", id, initiator);
		Game game = getGame(id);
		if (!game.getCreator().equals(initiator)) {
			LOGGER.error("failed to stop game with id={}, player={} is not game creator", id, initiator);
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
			game.sendGame();
			games.removeIf((o) -> o.getId() == id);
			maxId--;
		}
	}

	@Override
	public void addPlayer(int id, Player player) {
		LOGGER.info("adding player into game with id={}, player={}", id, player);
		Game game = getGame(id);
		if (!game.hasPlayer(player)) {
			game.addPlayer(player);
		} else {
			LOGGER.warn("failed add player to game, game already contains player, game={}, player={}", game, player);
			throw new NameBusyException("failed add player to game, game already contains "
					+ "player, game=" + game + ", player=" + player);
		}

	}

	@Override
	public Game addMessage(int id, Player player, String msg) {
		LOGGER.info("adding message to game with id={}, by player={}, msg={}", id, player, msg);
		Game game = getGame(id);
		if(game.getIsNight() && player.getRoleType() != RoleType.MAFIA) {
			LOGGER.warn("could not add message {} to chat at night for citizen player {}, game {}", msg, player, game);
			throw new IllegalMafiaRoleActionException("could not add message to chat at night for citizen player");
		}
		if (game.hasPlayer(player)) {
			game.getChat().addMessage(new Message(player.getName(), msg));
			game.sendGame();
		}
		return game;
	}
	
	@Override
	public void voteCitizen(int id, Player voter, Player target) {
		Game game = getGame(id);
		if(!game.hasPlayer(voter)) {
			LOGGER.warn("failed to vote citizen, not registered voter={}, game={}", voter, game);
			throw new UnauthorizedPlayerException(
					"Player {" + voter.getName() + "} is not registered in game {" + id + "}");
		}
		if(game.getIsNight()) {
			LOGGER.warn("failed to vote citizen during night, voter={}, game={}", game, voter);
			throw new VoteException("failed to vote citizen during day, game with id=" + id + ", voter=" + voter);
		}
		if(!voter.getIsAlive()) {
			LOGGER.warn("failed vote citizen, not alive voter={}, game={}", voter, game);
			throw new VoteException("failed to vote citizen, not alive voter=" + voter);
		}
		if(!target.getIsAlive()) {
			LOGGER.warn("failed vote citizen, not alive target={}, game={}", target, game);
			throw new VoteException("failed to vote citizen, not alive target=" + target);
		}
		LOGGER.info("vote citizen voter={}, target={}, game={}", voter, target, game);
		game.citizenVoteAgain(voter, target);
		sendGameToPlayers(game);
	}
	@Override
	public void voteMafia(int id, Player voter, Player target) {
		Game game = getGame(id);
		if(!game.hasPlayer(voter)) {
			LOGGER.warn("failed to vote mafia, not registered voter={}, game={}", voter, game);
			throw new UnauthorizedPlayerException(
					"Player {" + voter.getName() + "} is not registered in game {" + id + "}");
		}
		if(!game.getIsNight()) {
			LOGGER.warn("failed to vote mafia during day, voter={}, game={}", game, voter);
			throw new VoteException("failed to vote mafia during day, game with id=" + id + ", voter=" + voter);
		}
		if(!voter.getIsAlive()) {
			LOGGER.warn("failed vote mafia, not alive voter={}, game={}", voter, game);
			throw new VoteException("failed to vote mafia, not alive voter=" + voter);
		}
		if(!target.getIsAlive()) {
			LOGGER.warn("failed vote mafia, not alive target={}, game={}", target, game);
			throw new VoteException("failed to vote mafia, not alive target=" + target);
		}
		if(!voter.getRoleType().equals(RoleType.MAFIA)) {
			LOGGER.warn("failed vote mafia, voter is not mafia voter={}, game={}", voter, game);
			throw new VoteException("failed to vote mafia, voter is not mafia voter=" + target);
		}
		LOGGER.info("vote mafia voter={}, target={}, game={}", voter, target, game);
		game.mafiaVoteAgain(voter, target);
		sendGameToPlayers(game);
	}
	
	@Override
	public List<Game> getAccessibleGames() {
		return games.stream().filter((o) -> !o.getIsStarted() && !o.getIsFinished()).collect(Collectors.toList());
	}
	
	

	@Override
	public void removePlayer(int id, Player player) {
		Game game = getGame(id);
		game.removePlayer(player);
		LOGGER.debug("player {} removed from game {}", player, game);
		if(game.getPlayers().size() == 0) {
			games.removeIf((o) -> o.getId() == id);
			maxId--;
			LOGGER.info("removing game {} due it has no players", game);
		} else {
			if(game.getCreator().equals(player)) {
				Player newCreator = game.getPlayers().get(0);
				game.setCreator(newCreator);
				LOGGER.info("creator of game {} removed, changing creator to {}", game, newCreator);
			}
			game.sendGame();
		}
	}

	private void sendGameToPlayers(Game game) {
		game.sendGame();
	}
}
