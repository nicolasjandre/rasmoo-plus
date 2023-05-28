package com.client.ws.rasmooplus.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.client.ws.rasmooplus.domain.model.jpa.UserType;
import com.client.ws.rasmooplus.domain.repository.jpa.UserTypeRepository;

@ExtendWith(MockitoExtension.class)
class UserTypeServiceTest {

    @InjectMocks
    private UserTypeServiceImpl userTypeService;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Test
    void givenFindAll_whenThereAreDatasInDatabase_thenReturnAllDatas() {

        List<UserType> userTypeList = new ArrayList<>();
        UserType userType1 = new UserType(1L, "Professor", "Professor da plataforma");
        UserType userType2 = new UserType(2L, "Administrador", "Funcionário");

        Mockito.when(userTypeRepository.findAll()).thenReturn(userTypeList);

        userTypeList.add(userType1);
        userTypeList.add(userType2);

        var result = userTypeService.findAll();

        Assertions.assertThat(result).isNotEmpty().hasSize(2);
    }
}
