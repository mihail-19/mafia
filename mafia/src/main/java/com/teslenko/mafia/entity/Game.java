package com.teslenko.mafia.entity;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teslenko.mafia.exception.VoteException;
import com.teslenko.mafia.services.GameServiceImpl;
import com.teslenko.mafia.util.RandomRolesGenerator;

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
	public Game(int id,int dayTimeSeconds,int nightTimeSecods, Player creator, int mafiaNum) {
		this.id = id;
		this.dayTimeSeconds = dayTimeSeconds;
		this.nightTimeSeconds = nightTimeSecods;
		this.creator = creator;
		this.mafiaNum = mafiaNum;
		this.chat = new Chat(id);
	}
	
	public synchronized void addPlayer(Player player) {
		if(isStarted || isFinished) {
			throw new IllegalArgumentException("Game already started");
		}
		if(player == null) {
			throw new IllegalArgumentException("Could not add null player into game");
		}
		if(players.contains(player)) {
			throw new IllegalArgumentException("Player {" + player + "} already added into game");
		}
		players.add(player);
	}
	
	public boolean hasPlayer(Player player) {
		if(players.contains(player)) {
			return true;
		}
		return false;
	}
	
	public boolean killPlayerByVoteResults() {
		if(vote.isFinished()) {
			Player p = vote.findChosenPlayer();
			if(p != null) {
				p.setIsAlive(false);
				return true;
			}
		}
		return false;
	}
	
	public void startGame() {
		setRandomRoles();
		startTime = LocalTime.now();
		isStarted = true;
		vote = new Vote(players.size());
	}
	private void setRandomRoles() {
		int playersNum = players.size();
		int citizens = 0;
		int mafia = 0;
		SecureRandom rnd = new SecureRandom();
		if(playersNum == 1) {
			mafiaNum = 1;
			Player p = players.get(0);
			p.setRoleType(RoleType.CITIZEN);
			p.setMafiaRole(RandomRolesGenerator.generateCitizenRole());
		} else 	if(playersNum <= 3) {
			mafiaNum = 1;
			int mafiaIndex = rnd.nextInt(playersNum);
			Player pm = players.get(mafiaIndex);
			pm.setRoleType(RoleType.MAFIA);
			pm.setMafiaRole(RandomRolesGenerator.generateMafiaRole());
			for(int i = 0; i<playersNum; i++) {
				if(i != mafiaIndex) {
					Player ps = players.get(i);
					ps.setRoleType(RoleType.CITIZEN);
					ps.setMafiaRole(RandomRolesGenerator.generateCitizenRole());
				}
			}
		} else {
			Set<Integer> mafiaIndexes = new HashSet<>();
			while(mafiaIndexes.size() < mafiaNum) {
				int mafiaIndex = rnd.nextInt(playersNum);
				if(!mafiaIndexes.contains(mafiaIndex)) {
					mafiaIndexes.add(mafiaIndex);
				}
			}
			for(int i = 0; i<playersNum; i++) {
				Player p = players.get(i);
				if(mafiaIndexes.contains(i)) {
					p.setMafiaRole(RandomRolesGenerator.generateMafiaRole());
					p.setRoleType(RoleType.MAFIA);
					mafiaIndexes.remove(i);
				} else {
					p.setRoleType(RoleType.CITIZEN);
					p.setMafiaRole(RandomRolesGenerator.generateCitizenRole());
				}
			}
		}
	}
	public void citizenVoteAgain(Player voter, Player target) {
		throwIfDead(voter, target);
		if(players.contains(voter)) {
			vote.addVoice(voter, target);
		}
	}
	public void mafiaVoteAgain(Player voter, Player target) {
		if(voter.getRoleType().equals(RoleType.CITIZEN)) {
			LOGGER.error("Player {" + voter.getName() + "} is not from mafia");
			throw new VoteException("Player {" + voter.getName() + "} is not from mafia");
		}
		throwIfDead(voter, target);
		if(players.contains(voter)) {
			vote.addVoice(voter, target);
		}
	}
	private void throwIfDead(Player ... players) {
		for(Player p : players) {
			if(!p.getIsAlive()) {
				LOGGER.error("Player {" + p.getName() + "} is dead");
				throw new VoteException("Player {" + p.getName() + "} is dead");
			}
		}
	}
	public void resetVoteCitizen() {
		vote = new Vote(countAlive());
	}
	public void resetVoteMafia() {
		vote = new Vote(countAliveMafia());
	}
	public int countAlive() {
		int res = 0;
		for(Player p : players) {
			if(p.isAlive()) {
				res++;
			}
		}
		return res;
	}
	public int countAliveMafia() {
		int res = 0;
		for(Player p : players) {
			if(p.getIsAlive() && p.getRoleType().equals(RoleType.MAFIA)) {
				res++;
			}
		}
		return res;
	}
	
	/**
	 * Computes period finish time according to day/night length.
	 * @return
	 */
	public LocalTime finishTime() {
		if (!getIsNight()) {
			return getStartTime().plusSeconds(getDayTimeSeconds());
		} else {
			return getStartTime().plusSeconds(getNightTimeSeconds());
		}
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
	
	@Override
	public String toString() {
		return "Game [id=" + id + ", dayTimeSeconds=" + dayTimeSeconds + ", nightTimeSeconds=" + nightTimeSeconds
				+ ", startTime=" + startTime + ", mafiaNum=" + mafiaNum + "]";
	}
	
}
