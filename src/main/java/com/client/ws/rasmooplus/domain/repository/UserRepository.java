package com.client.ws.rasmooplus.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {   
}