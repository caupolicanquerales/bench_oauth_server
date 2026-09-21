package com.capo.bench_oauth_server.configuration;

import com.capo.bench_oauth_server.interfaces.UserDetailsService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.auth.issuer:http://localhost:8083}")
    private String issuerUri;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8082,https://bench-frontend.onrender.com,https://bench-api-gateway.onrender.com}")
    private String corsAllowedOrigins;

    @Value("${app.auth.rsa.key-id:bench-oauth-key-1}")
    private String rsaKeyId;

    @Value("${app.auth.rsa.public-key:}")
    private String rsaPublicKeyPem;

    @Value("${app.auth.rsa.private-key:}")
    private String rsaPrivateKeyPem;
    
    private final UserDetailsService userDetailsService;
    
    public SecurityConfig(UserDetailsService userDetailsService) {
    	this.userDetailsService= userDetailsService;
	}
    
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = 
                new OAuth2AuthorizationServerConfigurer();

        http
	        .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
	        .with(authorizationServerConfigurer, authorizationServer ->
	            authorizationServer
	                .oidc(Customizer.withDefaults()) // Enables OIDC discovery & endpoints
	        )
	        .authorizeHttpRequests(authorize ->
	            authorize
	                .anyRequest().authenticated()
	        )
	        .exceptionHandling(exceptions -> exceptions
	            .defaultAuthenticationEntryPointFor(
	                new LoginUrlAuthenticationEntryPoint("/login"),
	                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
	            )
	        )
	        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
	
	    return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/logout"))
            .requestCache(cache -> cache.requestCache(requestCache))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/register", "/logout", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico", "/error", "/.well-known/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")      
                .usernameParameter("username")     
                .passwordParameter("password")     
                .defaultSuccessUrl("/", false)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new OrRequestMatcher(
                    PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout"),
                    PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/logout")
                ))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // Use allowedOriginPatterns exclusively when allowCredentials is true
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        Set<String> redirectUris = new LinkedHashSet<>();
        // Default local redirect URIs always supported
        redirectUris.add("http://localhost:4200");
        redirectUris.add("http://localhost:4200/dashboard");
        redirectUris.add("http://localhost:4200/oauth2/callback");
        redirectUris.add("http://localhost:4200/login-callback");
        
        // Dynamically configured frontend URL (e.g., Render frontend)
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            String trimmedFrontend = frontendUrl.trim().replaceAll("/+$", "");
            redirectUris.add(trimmedFrontend);
            redirectUris.add(trimmedFrontend + "/dashboard");
            redirectUris.add(trimmedFrontend + "/oauth2/callback");
            redirectUris.add(trimmedFrontend + "/login-callback");
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("my-angular-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(false)
                        .build());

        for (String uri : redirectUris) {
            builder.redirectUri(uri);
            builder.postLogoutRedirectUri(uri);
        }

        return new InMemoryRegisteredClientRepository(builder.build());
    }

    @Bean
    public JWKSource jwkSource() {
        RSAKey rsaKey = loadOrGenerateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private RSAKey loadOrGenerateRsaKey() {
        if (rsaPublicKeyPem != null && !rsaPublicKeyPem.isBlank() &&
            rsaPrivateKeyPem != null && !rsaPrivateKeyPem.isBlank()) {
            try {
                RSAPublicKey publicKey = parsePublicKey(rsaPublicKeyPem);
                RSAPrivateKey privateKey = parsePrivateKey(rsaPrivateKeyPem);
                return new RSAKey.Builder(publicKey)
                        .privateKey(privateKey)
                        .keyID(rsaKeyId)
                        .build();
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to load RSA key pair from configuration", ex);
            }
        }

        // Fallback for local development or when environment variables are not configured
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(rsaKeyId != null && !rsaKeyId.isBlank() ? rsaKeyId : UUID.randomUUID().toString())
                .build();
    }

    private static RSAPublicKey parsePublicKey(String keyPem) throws Exception {
        String cleanPem = keyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(cleanPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private static RSAPrivateKey parsePrivateKey(String keyPem) throws Exception {
        String cleanPem = keyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(cleanPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA Key Pair", ex);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuerUri)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}