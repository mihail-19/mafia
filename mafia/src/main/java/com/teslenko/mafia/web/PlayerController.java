package com.teslenko.mafia.web;

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

@RestController
public class PlayerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);
	@Autowired
	private PlayerService playerService;
	@CrossOrigin
	@PostMapping("/login")
	public String add(@RequestParam String name) {
		LOGGER.info("registering player with name={}", name);
		Player res = playerService.createPlayer(name);
		return name;
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
	@PostMapping("/check-existance")
	public Boolean checkPlayer(@RequestParam String name) {
		LOGGER.info("checking player name={}", name);
		return !playerService.isFreeName(name);
	}
}
