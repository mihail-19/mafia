package com.teslenko.mafia.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.teslenko.mafia.exception.VoteException;

public class Vote {
	private int totalPlayers;
	private Map<Player, Player> voteMap = new HashMap<>();
	public Vote (int totalPlayers) {
		this.totalPlayers = totalPlayers;
	}
	public void addVoice(Player voter, Player target) {
		if(voteMap.containsKey(voter)) {
			throw new VoteException("Player {" + voter + "} already vote");
		}
		voteMap.put(voter, target);
	}
	
	public Player getChosenPlayer() {
		if(!isFinished()) {
			throw new VoteException("Vote not finished");
		}
		return choosePlayer();
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
		if(list.size() > 1 && list.get(0).getValue() == list.get(1).getValue()) {
				return null;
		} else {
			return list.get(0).getKey();
		}
		
	}
	public boolean isFinished() {
		if(voteMap.size() == totalPlayers) {
			return true;
		}
		return false;
	}
	
}
