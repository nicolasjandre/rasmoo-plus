package com.client.ws.rasmooplus.domain.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.jpa.UserType;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}