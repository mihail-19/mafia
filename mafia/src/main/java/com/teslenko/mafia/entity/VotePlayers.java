package com.teslenko.mafia.entity;

public class VotePlayers {
	Player voter;
	Player target;
	
	public VotePlayers(Player voter, Player target) {
		super();
		this.voter = voter;
		this.target = target;
	}
	public Player getVoter() {
		return voter;
	}
	public void setVoter(Player voter) {
		this.voter = voter;
	}
	public Player getTarget() {
		return target;
	}
	public void setTarget(Player target) {
		this.target = target;
	}
	
}
