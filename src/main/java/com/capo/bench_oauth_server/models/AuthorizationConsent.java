package com.capo.bench_oauth_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "oauth2_authorization_consent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationConsent {

    @EmbeddedId
    private AuthorizationConsentId id;

    @Column(name = "authorities", length = 1000, nullable = false)
    private String authorities;
}