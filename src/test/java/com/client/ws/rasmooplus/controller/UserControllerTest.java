package com.client.ws.rasmooplus.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.client.ws.rasmooplus.domain.mapper.UserMapper;
import com.client.ws.rasmooplus.domain.model.jpa.SubscriptionType;
import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.domain.model.jpa.UserType;
import com.client.ws.rasmooplus.domain.service.UserService;
import com.client.ws.rasmooplus.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(profiles = "test")
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void givenCreate_whenDtoIsValid_thenReturnUserCreated201() throws Exception {

        UserDto dto = new UserDto(null, "Teste Teste", "teste@teste.com", "21365987955", "81296871002", LocalDate.now(),
                LocalDate.now(), 1L, 1L);

        User user = UserMapper.fromDtoToEntity(dto, new UserType(), new SubscriptionType());

        when(userService.create(dto))
                .thenReturn(user);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(objectMapper.writeValueAsString(user)));

        verify(userService, times(1)).create(dto);
    }

    @Test
    void givenCreate_whenDtoCpfIsNotValid_thenThrowBadRequest400() throws Exception {

        UserDto dto = new UserDto(null, "Teste Teste", "teste@teste.com", "21365987955", "81226871002", LocalDate.now(),
                LocalDate.now(), 1L, 1L);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.message", is("[Campo 'CPF' precisa conter um CPF válido]")));

        verify(userService, times(0)).create(any());
    }
}
