package com.teslenko.mafia.entity;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.teslenko.mafia.util.RandomRolesGenerator;

/**
 * Sets random roles to players list.
 * @author Mykhailo Teslenko
 *
 */

public class MafiaRandomRolesSetter implements MafiaRoleSetter{
	
	private int mafiaNum;
	//If playersNum is lesser or equals this value, only one of them will be mafia
	private final static int PLAYERS_NUM_FOR_SINGLE_MAFIA = 3;
	public MafiaRandomRolesSetter(int mafiaNum) {
		this.mafiaNum = mafiaNum;
	}
	
	@Override
	public List<Player> setRolesToPlayers(List<Player> players) {
		int playersNum = players.size();
		
		if(playersNum == 0) {
			return players;
		}
		
		if(playersNum == 1) {
			return setForSinglePLayer(players);
		} else 	if(playersNum <= PLAYERS_NUM_FOR_SINGLE_MAFIA) {
			return setForMinPlayersNum(players, playersNum);
		} else {
			return setForLargePlayersNum(players, playersNum);
		}
	}
	
	@Override
	public int getActualMafiaNum() {
		return mafiaNum;
	}
	
	/*
	 * If single player, set his role to citizen, mafiaNum is ignored and set to 0.
	 */
	private List<Player> setForSinglePLayer(List<Player> players){
		mafiaNum = 0;
		Player p = players.get(0);
		p.setRoleType(RoleType.CITIZEN);
		p.setMafiaRole(RandomRolesGenerator.generateCitizenRole());
		return players;
	}
	
	/*
	 * Randomly chooses one player to be mafia 
	 */
	private List<Player> setForMinPlayersNum(List<Player> players, int playersNum){
		SecureRandom rnd = new SecureRandom();
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
		return players;
	}
	
	/*
	 * Chooses players with according to mafiaNum to have mafia role.
	 */
	private List<Player> setForLargePlayersNum(List<Player> players, int playersNum){
		Set<Integer> mafiaIndexes = prepareMafiaIndexesSet(playersNum);
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
		return players;
	}
	
	/*
	 * Chose randomly players who will have mafia role and return their indexes.
	 */
	private Set<Integer> prepareMafiaIndexesSet(int playersNum){
		SecureRandom rnd = new SecureRandom();
		Set<Integer> mafiaIndexes = new HashSet<>();
		while(mafiaIndexes.size() < mafiaNum) {
			int mafiaIndex = rnd.nextInt(playersNum);
			if(!mafiaIndexes.contains(mafiaIndex)) {
				mafiaIndexes.add(mafiaIndex);
			}
		}
		return mafiaIndexes;
	}
	
	
}
