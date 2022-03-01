package com.teslenko.mafia.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.teslenko.mafia.exception.ValidationException;
import com.teslenko.mafia.services.PlayerValidator;

public class PlayerValidationTest {
	
	@Test
	public void testEmpty() {
		PlayerValidator validator = new PlayerValidator();
		String nullName = null;
		String emptyName = "";
		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(nullName));
		assertThrows(ValidationException.class, () ->  validator.validateNameOrThrow(emptyName));
	}
	
	@Test
	public void testInvalidSymbols() {
		PlayerValidator validator = new PlayerValidator();
		String invalidName1 = "ab~#";
		String invalidName2 = "ab~#ew fw e";
		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(invalidName1));
		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(invalidName2));
	}
	
	@Test
	public void tooLarge() {
		PlayerValidator validator = new PlayerValidator();
		String invalidName = "abf dfs re g34  wewefwefwefwefafewf";
		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(invalidName));
	}
	
	//Not supported yet
	@Test
	public void testRestrictedNames() {
//		PlayerValidator validator = new PlayerValidator();
//		String adm = "ADMIN";
//		String adm1 = "ADMINva32";
//		String mafia1 = "efe mafial";
//		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(adm));
//		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(adm1));
//		assertThrows(ValidationException.class, () -> validator.validateNameOrThrow(mafia1));
	}
	@Test
	public void cyrrilycName() {
		PlayerValidator validator = new PlayerValidator();
		String cName1 = "Вася Пупкин";
		String cNameUkr = "Івае В'їздний 23";
		assertTrue(validator.validateNameOrThrow(cName1));
		assertTrue(validator.validateNameOrThrow(cNameUkr));
	}
	
}
