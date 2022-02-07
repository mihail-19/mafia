package com.teslenko.mafia.entity;

import org.springframework.security.core.GrantedAuthority;

public class MafiaGrantedAuthority implements GrantedAuthority{
	private static final String DEFAULT_ROLE = "ROLE_USER";
	private String role;
	public MafiaGrantedAuthority(String role) {
		this.role = role;
	}
	public MafiaGrantedAuthority() {
		this.role = DEFAULT_ROLE;
	}
	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return role;
	}

}
