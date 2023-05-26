package com.client.ws.rasmooplus.domain.service;

import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.dto.UserDto;

public interface UserService {
    
    User create(UserDto dto);
}
