package com.teslenko.mafia.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

public class AuthFilter extends GenericFilterBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);
	public static final String SESSION_PARAM_PLAYER_NAME = "name";
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOGGER.trace("Auth filter");
		HttpSession session = ((HttpServletRequest)request).getSession();
		if(session.getAttribute(SESSION_PARAM_PLAYER_NAME) == null) {
			LOGGER.error("No auth data in session");
			throw new UsernameNotFoundException("No auth data in session") ;
		}
		User user = new User(session.getAttribute(SESSION_PARAM_PLAYER_NAME).toString(), "123", Arrays.asList(new SimpleGrantedAuthority("User")));
		LOGGER.trace(user.getUsername());
		Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(request, response);
	}
	

}
