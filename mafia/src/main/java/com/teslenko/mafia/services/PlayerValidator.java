package com.teslenko.mafia.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.teslenko.mafia.exception.ValidationException;

@Component
public class PlayerValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerValidator.class);
	public static final int MAX_NAME_LENGTH = 30;
	public boolean validateNameOrThrow(String name) {
		LOGGER.info("validating player name {}", name);
		throwIfNullOrEmpty(name);
		throwIfInvalidSymbols(name);
		throwIfInvalidLength(name);
		return true;
	}
	
	private void throwIfNullOrEmpty(String name) {
		if(name == null || name.length() < 1) {
			LOGGER.warn("trying to create player with empty name {}", name);
			throw new ValidationException("name is empty");
		}
	}
	private void throwIfInvalidSymbols(String name) {
		if(!name.matches("^[0-9a-zA-Zа-яА-ЯіІ'їЇєЄ\\ _\\.]+$")) {
		
			LOGGER.warn("invalid player name {}", name);
			throw new ValidationException("invalid player name");
		}
	}
	private void throwIfInvalidLength(String name) {
		if(name.length() > MAX_NAME_LENGTH) {
			LOGGER.warn("name length is too big");
			throw new ValidationException("name length is too big");
		}
	}
}
