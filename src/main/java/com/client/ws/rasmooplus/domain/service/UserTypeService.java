package com.client.ws.rasmooplus.domain.service;

import java.util.List;

import com.client.ws.rasmooplus.domain.model.jpa.UserType;

public interface UserTypeService {
    
    List<UserType> findAll();
}
