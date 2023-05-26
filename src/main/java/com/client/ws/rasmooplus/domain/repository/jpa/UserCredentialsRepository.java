package com.client.ws.rasmooplus.domain.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.jpa.UserCredentials;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    Optional<UserCredentials> findByUsername(String username);
}
