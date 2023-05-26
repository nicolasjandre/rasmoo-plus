package com.client.ws.rasmooplus.domain.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.jpa.User;

public interface UserRepository extends JpaRepository<User, Long> {   
}