package com.teslenko.mafia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.GameCreateParams;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.exception.UnauthorizedPlayerException;
import com.teslenko.mafia.services.GameIdGenerator;
import com.teslenko.mafia.services.GameService;
import com.teslenko.mafia.services.GameServiceImpl;

@TestMethodOrder(OrderAnnotation.class)
public class GameServiceTest {

	private static GameService gameService;
	private static Player creator;
	private static int gameId;

	@BeforeAll
	public static void prepare() {
		SimpMessagingTemplate messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
		GameIdGenerator gameIdGenerator = new GameIdGenerator();
		gameService = new GameServiceImpl(messagingTemplate, gameIdGenerator);
		creator = new Player("creator");
	}

	@Test
	@Order(1)
	public void addingTest() {
		assertEquals(0, gameService.getAll().size());
		GameCreateParams gameCreateParams = GameCreateParams.builder().build();
		Game game = gameService.addGame(gameCreateParams, creator);
		gameId = 1;
		assertEquals(1, gameService.getAll().size());
		assertEquals(1, gameService.getAccessibleGames().size());
		assertEquals(game, gameService.getGame(gameId));
		// Trying to get game with inexistent ID
		assertThrows(NoSuchElementException.class, () -> gameService.getGame(123));
		
		//Adding other game
		GameCreateParams gameCreateParams1 = GameCreateParams.builder().build();
		Game game1 = gameService.addGame(gameCreateParams1, creator);
		assertEquals(2, game1.getId());
		assertEquals(2, gameService.getAll().size());
		assertEquals(game1, gameService.getGame(2));
	}

	@Test
	@Order(2)
	public void addPlayerTest() {
		assertFalse(gameService.getGame(gameId).getIsStarted());
		Player p1 = new Player("p1");
		gameService.addPlayer(1, p1);
		assertEquals(2, gameService.getGame(gameId).getPlayers().size());

	}

	@Test
	@Order(3)
	public void chatTest() {
		Player notJoinedPlayer = new Player("not joined");
		assertThrows(IllegalStateException.class, () -> gameService.addMessage(gameId, notJoinedPlayer, "hello"));
		final String msg = "message form creator";
		gameService.addMessage(gameId, creator, msg);
		assertEquals(2, gameService.getGame(gameId).getChat().getMessages().size());
	}

	@Test
	@Order(4)
	public void removingPlayer() {
		Player player = new Player("player remove");
		assertThrows(NoSuchElementException.class, () -> gameService.removePlayer(gameId, player));
		gameService.addPlayer(gameId, player);
		assertEquals(3, gameService.getGame(gameId).getPlayers().size());
		gameService.removePlayer(gameId, player);
		assertEquals(2, gameService.getGame(gameId).getPlayers().size());
		
		//Remove creator - should declare other as creator
		gameService.removePlayer(gameId, creator);
		assertEquals(1, gameService.getGame(gameId).getPlayers().size());
		Player lastPlayer = gameService.getGame(gameId).getPlayers().get(0);
		assertEquals(lastPlayer, gameService.getGame(gameId).getCreator());
		
		//Remove last player - game should be removed
		gameService.removePlayer(gameId, lastPlayer);
		assertThrows(NoSuchElementException.class, () -> gameService.getGame(gameId));
		assertEquals(1, gameService.getAll().size());
	}
	
	@Test
	@Order(5)
	public void recreation() {
		GameCreateParams gameCreateParams = GameCreateParams.builder().build();
		Game game = gameService.addGame(gameCreateParams, creator);
		assertEquals(1, game.getId());
	}
	
	@Test
	@Order(6)
	public void startTest() {
		GameCreateParams gameCreateParams = GameCreateParams.builder().build();
		Game game = gameService.addGame(gameCreateParams, creator);
		Player p2 = new Player("p2");
		gameService.addPlayer(game.getId(), p2);
		assertThrows(UnauthorizedPlayerException.class, () -> gameService.startGame(p2, game.getId()));
		gameService.startGame(creator, game.getId());
		assertTrue(game.getIsStarted());
		assertThrows(IllegalStateException.class, () -> gameService.startGame(creator, game.getId()));
	}
}
