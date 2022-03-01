package com.teslenko.mafia.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.services.PlayerService;
import com.teslenko.mafia.web.GameController;

@Component
public class PlayerLogoutHandler implements LogoutHandler{
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLogoutHandler.class);
	@Autowired
	private PlayerService service;
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if(authentication != null) {
			LOGGER.info("logout for player with name {}", authentication.getName());
			Player p = service.getPlayer(authentication.getName());
			service.remove(p.getId());
		} else {
			LOGGER.warn("trying to logout not authorized player, do nothing");
		}
	}

	
}
