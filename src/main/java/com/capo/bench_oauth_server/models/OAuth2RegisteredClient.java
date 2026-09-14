package com.capo.bench_oauth_server.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "oauth2_registered_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2RegisteredClient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique= true)
	private String cliendId;
	
	@CreationTimestamp
    @Column(
        name = "client_id_issued_at", 
        nullable = false, 
        updatable = false, 
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
	private Instant clientIdIssueAt;
	
	@Column(nullable = true)
	private String clientSecret;
	
	@CreationTimestamp
    @Column(
        name = "client_secret_expires_at", 
        nullable = true,  
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
	private Instant clientSecretExpiresAt;
	
	@Column(nullable = false)
	private String clientName;
	
	@Column(nullable = false)
	private String clientAuthenticationMethods;
	
	@Column(nullable = false)
	private String authorizationGrantTypes;
	
	@Column(nullable = true)
	private String redirectUris;
	
	@Column(nullable = true)
	private String logoutRedirectUris;
	
	@Column(nullable = false)
	private String scopes;
	
	@Column(nullable = false)
	private String clientSettings;
	
	@Column(nullable = false)
	private String tokenSettings;
	 
}
