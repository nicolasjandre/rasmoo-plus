package com.client.ws.rasmooplus.domain.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.jpa.SubscriptionType;

public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {

    Optional<SubscriptionType> findByProductKey(String productKey);
}