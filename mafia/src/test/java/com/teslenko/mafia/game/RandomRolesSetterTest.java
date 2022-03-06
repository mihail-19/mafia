package com.teslenko.mafia.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import com.teslenko.mafia.entity.MafiaRandomRolesSetter;
import com.teslenko.mafia.entity.MafiaRoleSetter;
import com.teslenko.mafia.entity.Player;
import com.teslenko.mafia.entity.RoleType;

public class RandomRolesSetterTest {
	
	@Test
	public void testZeroMafiaCount() {
		int mafiaNum = 3;
		MafiaRoleSetter roleSetter = new MafiaRandomRolesSetter(mafiaNum);
		List<Player> players = new ArrayList<>();
		players.add(new Player("p1"));
		roleSetter.setRolesToPlayers(players);
		assertEquals(RoleType.CITIZEN, players.get(0).getRoleType());
		assertEquals(0, roleSetter.getActualMafiaNum());
		
	
	}
	
	@Test
	public void testOneMafiaCount() {
		int mafiaNum = 3;
		List<Player> players = new ArrayList<>();
		players.add(new Player("p1"));
		players.add(new Player("p2"));
		players.add(new Player("p3"));
		MafiaRoleSetter roleSetter1 = new MafiaRandomRolesSetter(mafiaNum);
		roleSetter1.setRolesToPlayers(players);
		assertTrue(players.get(0).getRoleType() == RoleType.MAFIA 
				^ players.get(1).getRoleType() == RoleType.MAFIA 
				^ players.get(2).getRoleType() == RoleType.MAFIA);
		assertEquals(1, roleSetter1.getActualMafiaNum());
	}
	
	@Test
	public void testTwoMafiaCount() {
		int mafiaNum = 2;
		List<Player> players = new ArrayList<>();
		players.add(new Player("p1"));
		players.add(new Player("p2"));
		players.add(new Player("p3"));
		players.add(new Player("p3"));
		MafiaRoleSetter roleSetter1 = new MafiaRandomRolesSetter(mafiaNum);
		roleSetter1.setRolesToPlayers(players);
		int playersMafiaNum = 0;
		for(Player p : players) {
			if(p.getRoleType() == RoleType.MAFIA) {
				playersMafiaNum++;
			}
		}
		assertEquals(2, roleSetter1.getActualMafiaNum());
		assertEquals(2, playersMafiaNum);
	}
	
}
