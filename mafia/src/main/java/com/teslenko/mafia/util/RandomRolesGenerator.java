package com.teslenko.mafia.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.teslenko.mafia.entity.MafiaRole;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.RoleType;

public class RandomRolesGenerator {
	private static List<MafiaRole> mafiaRoles = new ArrayList<>();
	private static List<MafiaRole> citizenRoles = new ArrayList<>();
	/*
	 * Initiates lists of citizen and mafia roles. Should not be changed in runtime,
	 * so could be static.
	 */
	static {
		mafiaRoles.add(MafiaRole.MAFIOSY);
		
		citizenRoles.add(MafiaRole.BEAUTY);
		citizenRoles.add(MafiaRole.CITIZEN);
		citizenRoles.add(MafiaRole.SHERIF);
	}
	public static MafiaRole generateMafiaRole() {
		SecureRandom rnd = new SecureRandom();
		int roleIndex = rnd.nextInt(mafiaRoles.size());
		return mafiaRoles.get(roleIndex);
	}
	public static MafiaRole generateCitizenRole() {
		SecureRandom rnd = new SecureRandom();
		int roleIndex = rnd.nextInt(citizenRoles.size());
		return citizenRoles.get(roleIndex);
	}
}
