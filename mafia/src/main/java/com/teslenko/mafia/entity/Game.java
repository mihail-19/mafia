package com.teslenko.mafia.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teslenko.mafia.exception.VoteException;
import com.teslenko.mafia.services.GameSender;

public class Game {
	private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);
	private volatile int maxId = 1;
	private int id;
	private List<Player> players = new ArrayList<>();
	private Vote vote;
	private Player creator;
	private int dayTimeSeconds;
	private int nightTimeSeconds;
	private boolean isNight;
	private boolean isStarted;
	private boolean isFinished;
	private LocalTime startTime;
	private int mafiaNum;
	private Chat chat;
	private boolean isCitizenWin;
	@JsonIgnore
	private GameProcess gameProcess;
	@JsonIgnore
	private GameSender gameSender;

	public Game(int id, int dayTimeSeconds, int nightTimeSecods, Player creator, int mafiaNum) {
		this.id = id;
		this.dayTimeSeconds = dayTimeSeconds;
		this.nightTimeSeconds = nightTimeSecods;
		this.creator = creator;
		this.mafiaNum = mafiaNum;
		addPlayer(creator);
		chat = new Chat();
		chat.setGreetingMsg("-------- Новая игра! Общий чат -------------");
	}

	public synchronized void addPlayer(Player player) {
		LOGGER.info("adding player={} into game={}", player, this);
		if (isStarted || isFinished) {
			LOGGER.error("game is already started game={}", this);
			throw new IllegalStateException("Game already started");
		}
		if (player == null) {
			LOGGER.error("trying to add null player into game={}", this);
			throw new IllegalStateException("Could not add null player into game");
		}
		if (players.contains(player)) {
			LOGGER.error("trying to add existing player into game, player={}, game={}", player, this);
			throw new IllegalStateException("Player {" + player + "} already added into game");
		}
		players.add(player);
	}

	public boolean hasPlayer(Player player) {
		if (players.contains(player)) {
			return true;
		}
		return false;
	}

	/**
	 * Kills player according to vote results
	 * 
	 * @return
	 */
	public boolean killPlayerByVoteResults() {
		LOGGER.info("killing player for vote result for game={}", this);
		if (vote.isFinished()) {
			Player p = vote.findChosenPlayer();
			if (p != null) {
				LOGGER.info("player was killed by vote results player={}, game={}", p, this);
				p.setIsAlive(false);
				return true;
			}
		}
		LOGGER.info("No player was killed by vote results game={}", this);
		return false;
	}

	/**
	 * Trying to finish game according to players stay alive
	 */
	public void tryFinishGame() {
		int mafiaAlive = countAliveMafia();
		int totalAlive = countAlive();
		if (mafiaAlive == 0 && totalAlive != 0) {
			isFinished = true;
			isCitizenWin = true;
		} else if (mafiaAlive == totalAlive) {
			isFinished = true;
			isCitizenWin = false;
		}
	}

	/**
	 * Starts game - roles are given, day/night periods changes.
	 * 
	 * @param roleSetter
	 */
	public void startGame(MafiaRoleSetter roleSetter, GameProcess gameProcess) {
		this.gameProcess = gameProcess;
		players = roleSetter.setRolesToPlayers(players);
		mafiaNum = roleSetter.getActualMafiaNum();
		startTime = LocalTime.now();
		isStarted = true;
		vote = new Vote(players.size(), false);
		LOGGER.info("game is started, game={}", this);
		gameProcess.start();
	}

	public void citizenVoteAgain(Player voter, Player target) {
		if (!isStarted) {
			LOGGER.warn("Trying to vote while game is not started, game {}, voter {}", this, voter);
			throw new VoteException("Trying to vote while game is not started, game " + this + ", voter " + voter);
		}
		throwIfAnyIsDead(voter, target);
		if (players.contains(voter)) {
			vote.addVoice(voter, target);
		}
	}

	public void mafiaVoteAgain(Player voter, Player target) {
		if (!isStarted) {
			LOGGER.warn("Trying to vote while game is not started, game {}, voter {}", this, voter);
			throw new VoteException("Trying to vote while game is not started, game " + this + ", voter " + voter);
		}
		if (voter.getRoleType().equals(RoleType.CITIZEN)) {
			LOGGER.warn("Mafia vote error: voter {} is not from mafia", voter.getName());
			throw new VoteException("Mafia vote error: voter {" + voter.getName() + "} is not from mafia");
		}
		if (target.getRoleType().equals(RoleType.MAFIA)) {
			LOGGER.warn("Mafia vote error: vote target {} is from mafia, cannot kill", target.getName());
			throw new VoteException("Mafia vote target {" + target.getName() + "} is from mafia");
		}
		throwIfAnyIsDead(voter, target);
		if (players.contains(voter)) {
			vote.addVoice(voter, target);
		}
	}

	private void throwIfAnyIsDead(Player... players) {
		for (Player p : players) {
			if (!p.getIsAlive()) {
				LOGGER.warn("Player {} is already dead", p.getName());
				throw new VoteException("Player {" + p.getName() + "} is dead");
			}
		}
	}

	/**
	 * Resets vote for citizen vote.
	 **/

	public void resetVoteCitizen() {
		vote = new Vote(countAlive(), false);
	}

	/**
	 * Resets vote for mafia vote.
	 */
	public void resetVoteMafia() {
		vote = new Vote(countAliveMafia(), true);
	}

	/**
	 * Resets start time, chat, deleting all of the messages.
	 * Sets special greeting for period indication or game finish indication.
	 */
	public void startNewPeriod() {
		chat = new Chat();
		String greetings;
		if (isFinished) {
			if(isCitizenWin) {
				greetings = "-------- Игра завершена! Победили мирные жители -----------";
			} else {
				greetings = "-------- Игра завершена! Победила мафия -----------";
			}
		} else {
			if (isNight) {
				greetings = "--------------- Чат для мафии -------------------";
			} else {
				greetings = "---------------   Общий чат  --------------------";
			}
		}
		chat.setGreetingMsg(greetings);
		setStartTime(LocalTime.now());
	}

	/**
	 * Count all alive players.
	 **/
	public int countAlive() {
		int res = 0;
		for (Player p : players) {
			if (p.isAlive()) {
				res++;
			}
		}
		return res;
	}

	/**
	 * Count alive players with role MAFIA
	 * 
	 * @return
	 */
	public int countAliveMafia() {
		int res = 0;
		for (Player p : players) {
			if (p.getIsAlive() && p.getRoleType().equals(RoleType.MAFIA)) {
				res++;
			}
		}
		return res;
	}

	/**
	 * Computes period finish time according to day/night length.
	 * 
	 * @return
	 */
	public LocalTime periodFinishTime() {
		if (!getIsNight()) {
			return getStartTime().plusSeconds(getDayTimeSeconds());
		} else {
			return getStartTime().plusSeconds(getNightTimeSeconds());
		}
	}

	/**
	 * Method to communicate with game players
	 */
	public void sendGame() {
		// sending request to players to update game
		gameSender.sendRequestForGameRefresh();
	}

	/**
	 * Removes player from game.
	 * 
	 * @param player
	 */
	public void removePlayer(Player player) {
		players.removeIf((o) -> o.equals(player));
		sendGame();
	}

	public void stopGame() {
		isFinished = true;
	}

	public int getMaxId() {
		return maxId;
	}

	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public int getDayTimeSeconds() {
		return dayTimeSeconds;
	}

	public void setDayTimeSeconds(int dayTimeSeconds) {
		this.dayTimeSeconds = dayTimeSeconds;
	}

	public int getNightTimeSeconds() {
		return nightTimeSeconds;
	}

	public void setNightTimeSeconds(int nightTimeSeconds) {
		this.nightTimeSeconds = nightTimeSeconds;
	}

	public boolean getIsNight() {
		return isNight;
	}

	public void setIsNight(boolean isNight) {
		this.isNight = isNight;
	}

	public boolean getIsStarted() {
		return isStarted;
	}

	public void setIsStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public int getMafiaNum() {
		return mafiaNum;
	}

	public void setMafiaNum(int mafiaNum) {
		this.mafiaNum = mafiaNum;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
	}

	public int getId() {
		return id;
	}

	public GameProcess getGameProcess() {
		return gameProcess;
	}

	public void setGameProcess(GameProcess gameProcess) {
		this.gameProcess = gameProcess;
	}

	public GameSender getGameSender() {
		return gameSender;
	}

	public void setGameSender(GameSender gameSender) {
		this.gameSender = gameSender;
	}
	
	public void setIsCitizenWin(boolean isCitizenWin) {
		this.isCitizenWin = isCitizenWin;
	}
	
	public boolean getIsCitizenWin() {
		return isCitizenWin;
	}
	
	@Override
	public String toString() {
		return "Game [id=" + id + ", dayTimeSeconds=" + dayTimeSeconds + ", nightTimeSeconds=" + nightTimeSeconds
				+ ", startTime=" + startTime + ", mafiaNum=" + mafiaNum + "]";
	}

}
