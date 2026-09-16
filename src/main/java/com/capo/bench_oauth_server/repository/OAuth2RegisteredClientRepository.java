package com.capo.bench_oauth_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capo.bench_oauth_server.models.OAuth2RegisteredClient;

@Repository
public interface OAuth2RegisteredClientRepository  extends JpaRepository<OAuth2RegisteredClient, Long>{

}
