package com.client.ws.rasmooplus.domain.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.ws.rasmooplus.domain.dto.UserDto;
import com.client.ws.rasmooplus.domain.exception.BadRequestException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.mapper.UserMapper;
import com.client.ws.rasmooplus.domain.model.User;
import com.client.ws.rasmooplus.domain.model.UserType;
import com.client.ws.rasmooplus.domain.repository.UserRepository;
import com.client.ws.rasmooplus.domain.repository.UserTypeRepository;
import com.client.ws.rasmooplus.domain.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Override
    public User create(UserDto dto) {

        if (Objects.nonNull(dto.getId())) {
            throw new BadRequestException("Não pode haver um id no body");
        }

        var optionalUserType = userTypeRepository.findById(dto.getUserTypeId());

        if (optionalUserType.isEmpty()) {
            throw new NotFoundException("UserTypeId id=[" + dto.getUserTypeId() + "] não encontrado");
        }

        UserType userType = optionalUserType.get();
        User user = UserMapper.fromDtoToEntity(dto, userType, null);
        return userRepository.save(user);
    }
}
