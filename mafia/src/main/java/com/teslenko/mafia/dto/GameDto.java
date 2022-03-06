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

/**
 * DTO for {@link Game} for hiding fields according to player role.
 * @author Mykhailo Teslenko
 *
 */
public class GameDto {
	private static final Chat EMPTY_CHAT = new Chat();
	
	//Simply copied fiends
	private int id;
	private Player creator;
	private int dayTimeSeconds;
	private int nightTimeSeconds;
	private boolean isNight;
	private boolean isStarted;
	private boolean isFinished;
	private boolean isCitizenWin;
	private LocalTime startTime;
	private int mafiaNum;
	private Chat chat;
	
	//Fields copied with conditions
	private List<Player> players = new ArrayList<>();
	private Vote vote;
	
	/**
	 * Creates DTO copying fields from game according to {@link Player} receiver role.
	 * If game is finished, all fields are simply copied.
	 * @param game - game to transform
	 * @param receiver - {@link Player} to receive the game
	 */
	public GameDto(Game game, Player receiver) {
		simpleCopyFromGame(game);
		if(!game.getIsFinished()) {
			preparePlayers(game, receiver);
			prepareVote(game, receiver);
			prepareChat(game, receiver);
		} else {
			players = game.getPlayers();
			chat = game.getChat();
		}
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
		this.isCitizenWin = game.getIsCitizenWin();
	}
	
	/*
	 * Adding players  from {@link Game} List. If receiver is 
	 * mafia, players list is simply copied.
	 * If receiver is citizen, roleType of other players set to UNKNOWN.
	 */
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
	
	/*
	 * If citizen vote it is simply copied.
	 * If mafia vote, only mafia receiver will get real vote from game,
	 * citizens will get vote without voters list.
	 */
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
	
	/*
	 * Chat for citizens simply copied.
	 * Real mafia chat could be received only by mafia,
	 * citizens get empty chat to avoid NPE.
	 */
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
	
	public boolean getIsCitizenWin() {
		return isCitizenWin;
	}
}
