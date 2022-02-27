package com.teslenko.mafia.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler
	protected ResponseEntity<MafiaErrorResponse> globaHandle(RuntimeException ex, WebRequest request){
		return new ResponseEntity<MafiaErrorResponse>(new MafiaErrorResponse("Exception: " + ex.getMessage()), HttpStatus.EXPECTATION_FAILED);
	}
	private static class MafiaErrorResponse{
		private String message;
		public MafiaErrorResponse(String message) {
			this.message = message;
		}
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
}
