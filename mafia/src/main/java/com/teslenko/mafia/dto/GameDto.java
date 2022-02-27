package com.teslenko.mafia.dto;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.teslenko.mafia.entity.Chat;
import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.MafiaRole;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.RoleType;
import com.teslenko.mafia.entity.Vote;

public class GameDto {
	private static final Chat EMPTY_CHAT = new Chat();
	private int id;
	private Player creator;
	private int dayTimeSeconds;
	private int nightTimeSeconds;
	private boolean isNight;
	private boolean isStarted;
	private boolean isFinished;
	private LocalTime startTime;
	private int mafiaNum;
	private Chat chat;
	
	private List<Player> players = new ArrayList<>();
	private Vote vote;
	public GameDto(Game game, Player receiver) {
		simpleCopyFromGame(game);
		preparePlayers(game, receiver);
		prepareVote(game, receiver);
		prepareChat(game, receiver);
	}
	
	private void simpleCopyFromGame(Game game) {
		this.id = game.getId();
		this.creator = game.getCreator();
		this.dayTimeSeconds = game.getDayTimeSeconds();
		this.nightTimeSeconds = game.getNightTimeSeconds();
		this.isNight = game.getIsNight();
		this.isStarted = game.getIsStarted();
		this.isFinished = game.getIsFinished();
		this.startTime = game.getStartTime();
		this.mafiaNum = game.getMafiaNum();
	}
	private void preparePlayers(Game game, Player receiver){
		//Mafia player could know about all others
		if(receiver.getRoleType() == RoleType.MAFIA) {
			players = game.getPlayers();
			return;
		}
		for(Player p : game.getPlayers()) {
			if(p.getName().equals(receiver.getName())) {
				players.add(p);
			} else {
				Player modifiedPlayer = new Player(p.getName());
				modifiedPlayer.setAlive(p.getIsAlive());
				modifiedPlayer.setId(p.getId());
				modifiedPlayer.setMafiaRole(MafiaRole.UNKNOWN);
				modifiedPlayer.setRoleType(RoleType.UNKNOWN);
				players.add(modifiedPlayer);
			}
		}
	}
	private void prepareVote(Game game, Player receiver) {
		if(game.getVote() != null && game.getVote().getIsMafiaVote()) {
			if(receiver.getRoleType().equals(RoleType.MAFIA)) {
				vote = game.getVote();
			} else {
				Vote gameVote = game.getVote();
				vote = new Vote(gameVote.getTotalPlayers(), true);
				vote.setIsStarted(gameVote.getIsStarted());
				vote.setTimeStart(gameVote.getTimeStart());
				vote.setIsFinished(gameVote.getIsFinished());
			}
		} else {
			vote = game.getVote();
		}
	}
	private void prepareChat(Game game, Player receiver) {
		if(game.getIsNight()) {
			if(receiver.getRoleType() == RoleType.MAFIA) {
				chat = game.getChat();
			} else {
				chat = EMPTY_CHAT;
			}
		} else {
			chat = game.getChat();
		}
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
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

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}
	
}
