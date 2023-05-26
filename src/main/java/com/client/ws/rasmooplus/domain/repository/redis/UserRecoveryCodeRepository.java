package com.client.ws.rasmooplus.domain.repository.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.client.ws.rasmooplus.domain.model.redis.UserRecoveryCode;

public interface UserRecoveryCodeRepository extends CrudRepository<UserRecoveryCode, String> {

    Optional<UserRecoveryCode> findByEmail(String email);
}
