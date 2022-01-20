package com.teslenko.mafia.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.services.PlayerService;

@RestController
@RequestMapping("/players")
@SessionAttributes("player")
public class PlayerController {
	@Autowired
	private PlayerService playerService;
	
	@PostMapping("/add")
	public Player add(@ModelAttribute("player") Player player, @RequestParam String name) {
		Player res = playerService.createPlayer(name);
		ModelAndView m = new ModelAndView();
		m.addObject("player", res);
		return res;
	}
	
	@DeleteMapping("/logout")
	public void logout(@ModelAttribute("player") Player player, SessionStatus sessionStatus) {
		playerService.remove(player.getId());
		sessionStatus.setComplete();
	}
}
