package com.teslenko.mafia.web;

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
	@Autowired
	private PlayerService playerService;
	@GetMapping("")
	public void test() {
		System.out.println("test");
	}
	@CrossOrigin
	@PostMapping("/login")
	public String add(@RequestParam String name) {
		System.out.println("login");
		Player res = playerService.createPlayer(name);
		return name;
	}
	@CrossOrigin
	@GetMapping("/logout")
	public void logout(@RequestHeader("name") String name) {
		if(!playerService.isFreeName(name)) {
			playerService.remove(playerService.getPlayer(name).getId());
		}
	}
}
