package com.capo.bench_oauth_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capo.bench_oauth_server.models.UserDetail;

@Repository
public interface UserRepository extends JpaRepository<UserDetail, Long>{
	Optional<UserDetail> findByUsername(String username);
	
	Optional<UserDetail> findByEmail(String email);
	
	@Query("SELECT u FROM UserDetail u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<UserDetail> findByUsernameOrEmail(@Param("identifier") String identifier);
}
