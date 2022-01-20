package com.teslenko.mafia.entity;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.teslenko.mafia.util.RandomRolesGenerator;

public class Game {
	private volatile int maxId = 1;
	private int id;
	private List<Player> players;
	private Player creator;
	private int dayTimeSeconds;
	private int nightTimeSeconds;
	private boolean isNight;
	private boolean isStarted;
	private boolean isFinished;
	private LocalTime startTime;
	private int mafiaNum;
	public Game(int id,int dayTimeSeconds,int nightTimeSecods, Player creator, int mafiaNum) {
		this.id = id;
		this.dayTimeSeconds = dayTimeSeconds;
		this.nightTimeSeconds = nightTimeSecods;
		this.creator = creator;
		this.mafiaNum = mafiaNum;
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

	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
	}

	public int getId() {
		return id;
	}
	
	public void startGame() {
		setRandomRoles();
		startTime = LocalTime.now();
		isStarted = true;
	}
	private void setRandomRoles() {
		int playersNum = players.size();
		int citizens = 0;
		int mafia = 0;
		SecureRandom rnd = new SecureRandom();
		if(playersNum == 1) {
			mafiaNum = 1;
			players.get(0).setMafiaRole(RandomRolesGenerator.generateCitizenRole());
		} else 	if(playersNum <= 3) {
			mafiaNum = 1;
			int mafiaIndex = rnd.nextInt(playersNum);
			players.get(mafiaIndex).setMafiaRole(RandomRolesGenerator.generateMafiaRole());
			for(int i = 0; i<playersNum; i++) {
				if(i != mafiaIndex) {
					players.get(i).setMafiaRole(RandomRolesGenerator.generateCitizenRole());
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
				if(mafiaIndexes.contains(i)) {
					players.get(i).setMafiaRole(RandomRolesGenerator.generateMafiaRole());
					mafiaIndexes.remove(i);
				} else {
					players.get(i).setMafiaRole(RandomRolesGenerator.generateCitizenRole());
				}
			}
		}
	}
	public void stopGame() {
		isFinished = true;
	}
}
