package com.teslenko.mafia.exception;

public class NameBusyException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public NameBusyException(String message) {
		super(message);
	}
}
