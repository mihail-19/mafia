package com.teslenko.mafia.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.services.PlayerService;
import com.teslenko.mafia.services.PlayerValidator;

@RestController
public class PlayerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);
	@Autowired
	private PlayerService playerService;
	
	@GetMapping("/test")
	public String test() {
		return "Hello, world";
	}
	@CrossOrigin
	@GetMapping("/logout")
	public void logout(@RequestHeader("name") String name) {
		LOGGER.info("logout for player with name={}", name);
		if(!playerService.isFreeName(name)) {
			playerService.remove(playerService.getPlayer(name).getId());
		}
	}
	@CrossOrigin
	@GetMapping("/player-name")
	public String getPlayerName(Principal principal) {
		if(principal == null) {
			return "";
		}
		return principal.getName(); 
	}
	@CrossOrigin
	@PostMapping("/check-existance")
	public Boolean checkPlayer(@RequestParam String name) {
		LOGGER.info("checking player name={}", name);
		return !playerService.isFreeName(name);
	}
}
