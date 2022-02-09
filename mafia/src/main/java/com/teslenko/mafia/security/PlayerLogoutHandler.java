package com.teslenko.mafia.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.services.PlayerService;

@Component
public class PlayerLogoutHandler implements LogoutHandler{
	@Autowired
	private PlayerService service;
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Player p = service.getPlayer(authentication.getName());
		service.remove(p.getId());
	}

	
}
