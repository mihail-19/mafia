package com.teslenko.mafia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.teslenko.mafia.services.PlayerServiceImpl;
import com.teslenko.mafia.services.PlayerValidator;

public class AuthServiceTest {
	private static PlayerServiceImpl playerService;
	
	@BeforeAll
	public static void prepare() {
		PlayerValidator playerValidator = new PlayerValidator();
		playerService = new PlayerServiceImpl(playerValidator); 
	}
	@Test
	public void testCreate() {
		assertEquals(0, playerService.getAll().size());
		
		//Manual creation
		playerService.createPlayer("player1");
		assertEquals(1,  playerService.getAll().size());
		assertEquals(1, playerService.getAll().get(0).getId());
		assertEquals("player1", playerService.getAll().get(0).getName());
		
		//Creation as UserDetailsService
		assertInstanceOf(UserDetailsService.class, playerService);
		UserDetailsService uds = (UserDetailsService) playerService;
		uds.loadUserByUsername("loadedPlayer");
		assertEquals(2,  playerService.getAll().size());
		assertEquals(2, playerService.getAll().get(1).getId());
		assertEquals("loadedPlayer", playerService.getAll().get(1).getName());
		
		//Removing
		playerService.remove(1);
		assertEquals(1,  playerService.getAll().size());
		assertEquals(2, playerService.getAll().get(0).getId());
		assertEquals("loadedPlayer", playerService.getAll().get(0).getName());
	}
}
