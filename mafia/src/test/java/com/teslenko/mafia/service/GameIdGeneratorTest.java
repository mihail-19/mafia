package com.teslenko.mafia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.teslenko.mafia.services.GameIdGenerator;

public class GameIdGeneratorTest {
	
	@Test
	public void testIdGeneration() {
		GameIdGenerator generator = new GameIdGenerator();
		assertEquals(1, generator.generateId());
		assertEquals(2, generator.generateId());
		assertEquals(3, generator.generateId());
		generator.releaseId(2);
		assertEquals(2, generator.generateId());
		assertEquals(4, generator.generateId());
		generator.releaseId(4);
		assertEquals(4, generator.generateId());
		
		//Invalid id to remove
		assertThrows(IllegalArgumentException.class, () -> generator.releaseId(123));
		assertThrows(IllegalArgumentException.class, () -> generator.releaseId(0));
		generator.releaseId(2);
		assertThrows(IllegalArgumentException.class, () -> generator.releaseId(2));
		
	}
}
