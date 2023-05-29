package com.client.ws.rasmooplus.controller;

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

import com.client.ws.rasmooplus.domain.service.impl.UserCredentialsServiceImpl;
import com.client.ws.rasmooplus.dto.UserCredentialsDto;
import com.client.ws.rasmooplus.dto.UserRecoveryCodeDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@WebMvcTest(UserRecoveryCodeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(profiles = "test")
public class UserRecoveryCodeControllerTest {

    @MockBean
    private UserCredentialsServiceImpl userCredentialsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenSendRecoveryCode_whenEmailIsInvalid_thenThrowBadRequest400() throws Exception {

        UserRecoveryCodeDto dto = new UserRecoveryCodeDto("teste");

        mockMvc.perform(post("/user-recovery-code/send")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())));

        verify(userCredentialsService, times(0)).sendRecoveryCode(any());
    }

    @Test
    void givenSendRecoveryCode_whenEmailIsOk_thenReturnVoid200() throws Exception {

        UserRecoveryCodeDto dto = new UserRecoveryCodeDto("teste@teste.com");

        mockMvc.perform(post("/user-recovery-code/send")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userCredentialsService, times(1)).sendRecoveryCode(any());
    }

    @Test
    void givenIsRecoveryCodeValid_whenEverythingOk_thenReturnTrue200() throws Exception {

        when(userCredentialsService.isRecoveryCodeValid("4183", "teste@teste.com")).thenReturn(true);

        mockMvc.perform(get("/user-recovery-code/valid?recoveryCode=4183&email=teste@teste.com"))
                .andExpect(jsonPath("$", is(true)))
                .andExpect(status().isOk());


        verify(userCredentialsService, times(1)).isRecoveryCodeValid("4183", "teste@teste.com");
    }

    @Test
    void givenUpdatePasswordByRecoveryCode_whenEverythingOk_thenReturnVoid204() throws Exception {

        UserCredentialsDto dto = new UserCredentialsDto("teste@teste.com", "1234", "1234");

        mockMvc.perform(patch("/user-recovery-code/password?recoveryCode=4183")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(userCredentialsService, times(1)).updatePasswordByRecoveryCode(dto, "4183");
    }

    @Test
    void givenUpdatePasswordByRecoveryCode_whenDtoMissingValues_thenThrowBadRequest400() throws Exception {

        UserCredentialsDto dto = new UserCredentialsDto("teste@teste.com", "", "1234");

        mockMvc.perform(patch("/user-recovery-code/password?recoveryCode=4183")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(status().isBadRequest());

        verify(userCredentialsService, times(0)).updatePasswordByRecoveryCode(any(), any());
    }
}
