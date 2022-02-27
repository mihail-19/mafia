package com.teslenko.mafia.entity;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.teslenko.mafia.exception.VoteException;

public class Vote {
	private int totalPlayers;
	private Set<VotePlayers> voteMap = new HashSet<>();
	private boolean isStarted;
	private boolean isFinished;
	private LocalTime timeStart;
	private boolean isMafiaVote;
	public final int voteTimeSeconds = 15;
	public Vote (int totalPlayers, boolean isMafiaVote) {
		this.totalPlayers = totalPlayers;
		this.isMafiaVote = isMafiaVote;
	}
	public void startVote() {
		isStarted = true;
		voteMap = new HashSet<>();
		timeStart = LocalTime.now();
	}
	public void addVoice(Player voter, Player target) {
		VotePlayers votePlayers = new VotePlayers(voter, target);
		if(!isStarted) {
			throw new VoteException("Vote is not started");
		}
		if(voteMap.contains(votePlayers)) {
			throw new VoteException("Player {" + voter + "} already vote");
		}
		voteMap.add(new VotePlayers(voter, target));
	}
	
	public Player findChosenPlayer() {
		if(!isFinished()) {
			throw new VoteException("Vote is not finished");
		}
		return choosePlayer();
	}
	public boolean isFinished() {
		if(!isStarted) {
			return false;
		}
		if(voteMap.size() == totalPlayers || LocalTime.now().isAfter(timeStart.plusSeconds(voteTimeSeconds))) {
			isFinished = true;
			return true;
		}
		return false;
	}
	
	private Player choosePlayer() {
		Map<Player, Integer> res = new HashMap<>();
		for(VotePlayers vp : voteMap) {
			Player p = vp.getTarget();
			if(res.containsKey(p)) {
				res.put(p, res.get(p));
			} else {
				res.put(p, 1);
			}
		}
		List<Map.Entry<Player, Integer>> list = res.entrySet()
				.stream()
				.sorted((o1, o2) -> o2.getValue() - o1.getValue())
				.collect(Collectors.toList());
		if(list.size() == 0 || list.size() > 1 && list.get(0).getValue() == list.get(1).getValue()) {
				return null;
		} else {
			return list.get(0).getKey();
		}
		
	}
	
	public Set<VotePlayers> getVoteMap() {
		return voteMap;
	}
	public void setVoteMap(Set<VotePlayers> voteMap) {
		this.voteMap = voteMap;
	}
	public int getTotalPlayers() {
		return totalPlayers;
	}
	public void setTotalPlayers(int totalPlayers) {
		this.totalPlayers = totalPlayers;
	}
	public boolean getIsStarted() {
		return isStarted;
	}
	public void setIsStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}
	public LocalTime getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(LocalTime timeStart) {
		this.timeStart = timeStart;
	}
	public int getVoteTime() {
		return voteTimeSeconds;
	}
	public boolean getIsFinished() {
		return isFinished;
	}
	public boolean getIsMafiaVote() {
		return isMafiaVote;
	}
	
	/**
	 * Method should be used only for DTO copying
	 * @param isFinished
	 */
	public void setIsFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	public void finish() {
		if(!isStarted) {
			throw new IllegalStateException("Could not finish vote for it is not started");
		}
		this.isFinished = true;
	}
	@Override
	public String toString() {
		return "Vote [totalPlayers=" + totalPlayers + ", voteMap=" + voteMap + ", isStarted=" + isStarted
				+ ", timeStart=" + timeStart + ", voteTimeSeconds=" + voteTimeSeconds + "]";
	}
	
	
	
}
