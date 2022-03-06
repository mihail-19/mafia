package com.teslenko.mafia.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.GameProcess;
import com.teslenko.mafia.entity.MafiaRandomRolesSetter;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.RoleType;
import com.teslenko.mafia.entity.Vote;
import com.teslenko.mafia.exception.VoteException;
import com.teslenko.mafia.services.GameSender;

public class GameTests {
	private static Game game;
	private static Player creator;
	private static GameSender gameSenderMock;
	@BeforeAll
	public static void prepare() {
		creator = new Player("creator player");
		game = new Game(11, 10, 10, creator, 1);
		gameSenderMock = Mockito.mock(GameSender.class);
	}
	
	@Test
	public void PlayerAddingTest() {
		Game game = new Game(2, 10, 10, creator, 1);
		assertTrue(game.hasPlayer(creator));
		
		Player player = new Player("player1");
		Player playerEqual = new Player("player1");
		assertEquals(1, game.getPlayers().size());
		game.addPlayer(player);
		assertTrue(game.hasPlayer(player));
		assertEquals(2, game.getPlayers().size());
		assertThrows(IllegalStateException.class, () -> game.addPlayer(player));
		assertThrows(IllegalStateException.class, () -> game.addPlayer(playerEqual));
		
		assertEquals(2, game.getPlayers().size());
		game.startGame(new MafiaRandomRolesSetter(1), new GameProcess(game));
		Player player2 = new Player("player2");
		assertThrows(IllegalStateException.class, () -> game.addPlayer(player2));
		assertThrows(IllegalStateException.class, () -> game.addPlayer(null));
		
		
	}
	
	@Test
	public void playerRemovingTest() {
		Game game = new Game(3, 10, 10, creator, 1);
		game.setGameSender(gameSenderMock);
		Player p = new Player("p1");
		game.addPlayer(p);
		assertTrue(game.hasPlayer(p));
		game.removePlayer(p);
		assertFalse(game.hasPlayer(p));
		assertEquals(1, game.countAlive());
	}
	
	@Test
	public void voteTest() {
		Game game = new Game(3, 10, 10, creator, 1);
		game.setGameSender(gameSenderMock);
		Player p1 = new Player("p1");
		game.addPlayer(p1);
		assertThrows(VoteException.class, () -> game.citizenVoteAgain(creator, p1));
		assertEquals(2, game.countAlive());
		
		GameProcess gameProcessMock = Mockito.mock(GameProcess.class);
		game.startGame(new MafiaRandomRolesSetter(1), gameProcessMock);
		assertThrows(VoteException.class, () -> game.citizenVoteAgain(creator, p1));
	}
	
	@Test
	public void testVoteResults() {
		Game game = new Game(3, 10, 10, creator, 1);
		game.setGameSender(gameSenderMock);
		Player p1 = new Player("p1");
		game.addPlayer(p1);
		GameProcess gameProcessMock = Mockito.mock(GameProcess.class);
		game.startGame(new MafiaRandomRolesSetter(1), gameProcessMock);
		Vote vote = new Vote(game.countAlive(), false);
		vote.startVote();
		game.setVote(vote);
		game.citizenVoteAgain(creator, p1);
		vote.finish();
		assertTrue(game.killPlayerByVoteResults());
		
		assertFalse(p1.getIsAlive());
		assertTrue(creator.getIsAlive());
		assertThrows(VoteException.class, () -> game.citizenVoteAgain(creator, p1));
		assertThrows(VoteException.class, () -> game.citizenVoteAgain(p1, creator));
		
		//Trying to finish game
		assertEquals(1, game.countAlive());
		game.tryFinishGame();
		assertTrue(game.getIsFinished());
		RoleType creatorRoleType = creator.getRoleType();
		if(creatorRoleType == RoleType.CITIZEN) {
			assertTrue(game.getIsCitizenWin());
		} else {
			assertFalse(game.getIsCitizenWin());
		}
		
	}
}
