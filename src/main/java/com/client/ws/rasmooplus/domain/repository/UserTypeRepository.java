package com.client.ws.rasmooplus.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.UserType;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}