package com.teslenko.mafia.dto;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.Vote;

public class VoteDto{
	private int totalPlayers;
	private Map<String, String> voteMap;
	private boolean isStarted;
	private LocalTime timeStart;
	public int voteTimeSeconds;
	public VoteDto(Vote vote) {
		totalPlayers = vote.getTotalPlayers();
		isStarted = vote.getIsStarted();
		timeStart = vote.getTimeStart();
		voteTimeSeconds = vote.getVoteTime();
		voteMap = new HashMap<>();
		for(Map.Entry<Player, Player> e : vote.getVoteMap().entrySet()) {
			voteMap.put(e.getKey().getName(), e.getValue().getName());
		}
				
	}
	public int getTotalPlayers() {
		return totalPlayers;
	}
	public void setTotalPlayers(int totalPlayers) {
		this.totalPlayers = totalPlayers;
	}
	public Map<String, String> getVoteMap() {
		return voteMap;
	}
	public void setVoteMap(Map<String, String> voteMap) {
		this.voteMap = voteMap;
	}
	public boolean isStarted() {
		return isStarted;
	}
	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}
	public LocalTime getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(LocalTime timeStart) {
		this.timeStart = timeStart;
	}
	public int getVoteTimeSeconds() {
		return voteTimeSeconds;
	}
	public void setVoteTimeSeconds(int voteTimeSeconds) {
		this.voteTimeSeconds = voteTimeSeconds;
	}
	
}
