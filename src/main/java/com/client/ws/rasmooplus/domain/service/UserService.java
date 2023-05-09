package com.client.ws.rasmooplus.domain.service;

import com.client.ws.rasmooplus.domain.dto.UserDto;
import com.client.ws.rasmooplus.domain.model.User;

public interface UserService {
    
    User create(UserDto dto);
}
