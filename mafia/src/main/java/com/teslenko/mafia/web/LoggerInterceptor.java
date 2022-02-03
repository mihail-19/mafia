package com.teslenko.mafia.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggerInterceptor  implements HandlerInterceptor{
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String name = request.getHeader("name");
		LOGGER.info("request for player name=" + name);
		return true;
	}

}
