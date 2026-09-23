package com.capo.bench_oauth_server.interfaces;

import org.springframework.security.core.Authentication;

import com.capo.bench_oauth_server.dto.RegistrationRequest;

public interface LoginService {
	String register(RegistrationRequest registrationRequest);
	String resolveRootView(Authentication authentication);
	String resolveLoginView(Authentication authentication);
}