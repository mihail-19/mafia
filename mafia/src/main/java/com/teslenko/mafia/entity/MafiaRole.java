package com.teslenko.mafia.entity;

public enum MafiaRole {
	
	CITIZEN(RoleType.CITIZEN), BEAUTY(RoleType.CITIZEN), SHERIF(RoleType.CITIZEN), MAFIOSY(RoleType.MAFIA);
	
	private MafiaRole(RoleType roleType) {
		this.roleType = roleType;
	}
	private RoleType roleType;
	public RoleType getRoleType() {
		return roleType;
	}
}
