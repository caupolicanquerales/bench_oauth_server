package com.capo.bench_oauth_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capo.bench_oauth_server.models.UserDetail;

@Repository
public interface UserRepository extends JpaRepository<UserDetail, Long>{
	Optional<UserDetail> findByUsername(String username);
}
