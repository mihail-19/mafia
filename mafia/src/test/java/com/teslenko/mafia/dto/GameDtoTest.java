package com.teslenko.mafia.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.teslenko.mafia.entity.Game;
import com.teslenko.mafia.entity.GameProcess;
import com.teslenko.mafia.entity.MafiaRoleSetter;
import com.teslenko.mafia.entity.Message;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.RoleType;

public class GameDtoTest {
	private static Player creator;
	private static Player mafiaPlayer;
	private static Game game;
	private static MafiaRoleSetter roleSetter;
	private static GameProcess gp;
	@BeforeAll
	public static void prepare() {
		creator = new Player("creator");
		mafiaPlayer = new Player("mafia player");
		game = new Game(1, 10, 10, creator, 1);
		game.addPlayer(mafiaPlayer);
		gp = Mockito.mock(GameProcess.class);
		roleSetter = Mockito.mock(MafiaRoleSetter.class);
		List<Player> gamePlayers = game.getPlayers();
		Mockito.when(roleSetter.setRolesToPlayers(gamePlayers)).thenReturn(gamePlayers);
		Mockito.when(roleSetter.getActualMafiaNum()).thenReturn(1);
		Message msg = new Message(creator.getName(), "msg");
		game.getChat().addMessage(msg);
		
		GameDto gameDtoBeforeStart = new GameDto(game, creator);
		assertEquals(RoleType.UNKNOWN, gameDtoBeforeStart.getPlayers().get(0).getRoleType());
		assertEquals(RoleType.UNKNOWN, gameDtoBeforeStart.getPlayers().get(1).getRoleType());
		assertTrue(gameDtoBeforeStart.getChat().getMessages().contains(msg));
		
		game.startGame(roleSetter, gp);
		creator.setRoleType(RoleType.CITIZEN);
		mafiaPlayer.setRoleType(RoleType.MAFIA);
	}
	
	@Test
	public void testRolesAfterStart() {
		assertEquals(RoleType.CITIZEN, game.getPlayers().get(0).getRoleType());
		assertEquals(RoleType.MAFIA, game.getPlayers().get(1).getRoleType());
		
		GameDto gameDtoAfterStart = new GameDto(game, creator);
		assertEquals(RoleType.CITIZEN, gameDtoAfterStart.getPlayers().get(0).getRoleType());
		assertEquals(RoleType.UNKNOWN, gameDtoAfterStart.getPlayers().get(1).getRoleType());
		
		GameDto gameDtoAfterStartMafia = new GameDto(game, mafiaPlayer);
		assertEquals(RoleType.CITIZEN, gameDtoAfterStartMafia.getPlayers().get(0).getRoleType());
		assertEquals(RoleType.MAFIA, gameDtoAfterStartMafia.getPlayers().get(1).getRoleType());
	}
	
	@Test
	public void accessAtNightTest() {
		game.setIsNight(true);
		game.resetVoteMafia();
		game.getVote().startVote();
		Message nightMsg = new Message("mafiaPlayer", "nightMsg");
		game.getChat().addMessage(nightMsg);
		game.mafiaVoteAgain(mafiaPlayer, creator);
		
		//Citizen
		GameDto gameDtoNightCitizen = new GameDto(game, creator);
		//Role access
		assertEquals(RoleType.CITIZEN, gameDtoNightCitizen.getPlayers().get(0).getRoleType());
		assertEquals(RoleType.UNKNOWN, gameDtoNightCitizen.getPlayers().get(1).getRoleType());
		//Chat access (citizen)
		assertFalse(gameDtoNightCitizen.getChat().getMessages().contains(nightMsg));
		//Vote access (citizen)
		assertEquals(0, gameDtoNightCitizen.getVote().getVoteMap().size());
		
		//Mafia
		GameDto gameDtoNightMafia = new GameDto(game, mafiaPlayer);
		//Chat access
		assertTrue(gameDtoNightMafia.getChat().getMessages().contains(nightMsg));
		//Vote access
		assertEquals(1, gameDtoNightMafia.getVote().getVoteMap().size());
	}
}
