package com.capo.bench_oauth_server.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.capo.bench_oauth_server.security.AuthenticatedUser;

class SecurityConfigJwtTokenCustomizerTest {

    @Test
    void resolveUserIdReturnsDatabaseUserIdFromAuthenticatedPrincipal() {
        AuthenticatedUser principal = new AuthenticatedUser(
                42L,
                "athlete@bench.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_ATHLETE"), new SimpleGrantedAuthority("ROLE_COACH"))
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        assertThat(SecurityConfig.resolveUserId(authentication)).isEqualTo(42L);
    }
}
