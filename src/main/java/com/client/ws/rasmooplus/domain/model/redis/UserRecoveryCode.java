package com.client.ws.rasmooplus.domain.model.redis;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("userRecoveryCode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecoveryCode {
    
    @Id
    private String id;

    @Indexed
    private String email;

    private String code;

    private boolean alreadyUsed = false;
    
    private LocalDateTime creationDate = LocalDateTime.now();
}
