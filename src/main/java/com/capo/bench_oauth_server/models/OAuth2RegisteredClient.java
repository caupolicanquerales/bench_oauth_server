package com.capo.bench_oauth_server.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "oauth2_registered_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2RegisteredClient {
	@Id
    @Column(length = 100)
    private String id;
	
	@Column(name= "client_id",nullable = false, unique= true)
	private String clientId;
	
	@CreationTimestamp
    @Column(
        name = "client_id_issued_at", 
        nullable = false, 
        updatable = false, 
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
	private Instant clientIdIssuedAt;
	
	@Column(name= "client_secret", nullable = true)
	private String clientSecret;
	
	@CreationTimestamp
    @Column(
        name = "client_secret_expires_at", 
        nullable = true,  
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
	private Instant clientSecretExpiresAt;
	
	@Column(name="client_name", nullable = false)
	private String clientName;
	
	@Column(name="client_authentication_methods", nullable = false)
	private String clientAuthenticationMethods;
	
	@Column(name="authorization_grant_types", nullable = false)
	private String authorizationGrantTypes;
	
	@Column(name="redirect_uris", nullable = true)
	private String redirectUris;
	
	@Column(name="logout_redirect_uris", nullable = true)
	private String logoutRedirectUris;
	
	@Column(name="scopes", nullable = false)
	private String scopes;
	
	@Column(name="client_settings",nullable = false)
	private String clientSettings;
	
	@Column(name="token_settings", nullable = false)
	private String tokenSettings;
	 
}
