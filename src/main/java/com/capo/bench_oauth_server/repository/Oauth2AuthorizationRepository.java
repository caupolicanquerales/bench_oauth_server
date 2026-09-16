package com.capo.bench_oauth_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capo.bench_oauth_server.models.Oauth2Authorization;

@Repository
public interface Oauth2AuthorizationRepository extends JpaRepository<Oauth2Authorization, String>{

}
