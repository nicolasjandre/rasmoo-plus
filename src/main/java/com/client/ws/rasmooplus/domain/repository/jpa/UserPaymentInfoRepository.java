package com.client.ws.rasmooplus.domain.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.client.ws.rasmooplus.domain.model.jpa.UserPaymentInfo;

public interface UserPaymentInfoRepository extends JpaRepository<UserPaymentInfo, Long> {   
}