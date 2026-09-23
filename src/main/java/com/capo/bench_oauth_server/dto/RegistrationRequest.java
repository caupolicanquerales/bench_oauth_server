package com.capo.bench_oauth_server.dto;

public record RegistrationRequest(
        String email,
        String registerPassword,
        String confirmPassword,
        String fullName,
        String portalRole
) {
}