package com.capo.bench_oauth_server.interfaces;

public interface LoginService {
	String validationProcessRegister(String email, String password, String confirmPassword, String fullName, String portalRole);
}
