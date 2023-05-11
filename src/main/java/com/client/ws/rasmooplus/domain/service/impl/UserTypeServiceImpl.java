package com.client.ws.rasmooplus.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.ws.rasmooplus.domain.model.UserType;
import com.client.ws.rasmooplus.domain.repository.UserTypeRepository;
import com.client.ws.rasmooplus.domain.service.UserTypeService;

@Service
public class UserTypeServiceImpl implements UserTypeService {

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Override
    public List<UserType> findAll() {
        return userTypeRepository.findAll();
    }

}
