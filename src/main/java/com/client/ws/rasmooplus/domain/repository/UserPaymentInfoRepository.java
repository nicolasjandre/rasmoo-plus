package com.client.ws.rasmooplus.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.UserPaymentInfo;

public interface UserPaymentInfoRepository extends JpaRepository<UserPaymentInfo, Long> {   
}