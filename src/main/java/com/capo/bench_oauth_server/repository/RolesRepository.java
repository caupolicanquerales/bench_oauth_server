package com.capo.bench_oauth_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capo.bench_oauth_server.models.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

}
