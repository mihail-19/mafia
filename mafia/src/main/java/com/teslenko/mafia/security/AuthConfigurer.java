package com.teslenko.mafia.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
	
	@Override
	public void configure(HttpSecurity builder) throws Exception {
		AuthFilter filter = new AuthFilter();
		builder.addFilterBefore(filter,  UsernamePasswordAuthenticationFilter.class);
	}
}
