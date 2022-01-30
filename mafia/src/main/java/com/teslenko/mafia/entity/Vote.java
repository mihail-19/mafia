package com.teslenko.mafia.entity;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.teslenko.mafia.exception.VoteException;

public class Vote {
	private int totalPlayers;
	private Map<Player, Player> voteMap = new HashMap<>();
	private boolean isStarted;
	private LocalTime timeStart;
	public final int voteTimeSeconds = 30;
	public Vote (int totalPlayers) {
		this.totalPlayers = totalPlayers;
	}
	public void startVote() {
		isStarted = true;
		voteMap = new HashMap<>();
		timeStart = LocalTime.now();
	}
	public void addVoice(Player voter, Player target) {
		if(!isStarted) {
			throw new VoteException("Vote is not started");
		}
		if(voteMap.containsKey(voter)) {
			throw new VoteException("Player {" + voter + "} already vote");
		}
		voteMap.put(voter, target);
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
			return true;
		}
		return false;
	}
	
	private Player choosePlayer() {
		Map<Player, Integer> res = new HashMap<>();
		for(Player p : voteMap.values()) {
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
	public Map<Player, Player> getVoteMap() {
		return voteMap;
	}
	public void setVoteMap(Map<Player, Player> voteMap) {
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
	@Override
	public String toString() {
		return "Vote [totalPlayers=" + totalPlayers + ", voteMap=" + voteMap + ", isStarted=" + isStarted
				+ ", timeStart=" + timeStart + ", voteTimeSeconds=" + voteTimeSeconds + "]";
	}
	
	
	
}
