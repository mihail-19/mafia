package com.teslenko.mafia.exception;

public class UnauthorizedPlayerException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

		public UnauthorizedPlayerException(String msg) {
			super(msg);
		}
}
