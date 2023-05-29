package com.client.ws.rasmooplus.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.client.ws.rasmooplus.domain.service.PaymentInfoService;
import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.dto.UserPaymentInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@WebMvcTest(PaymentInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(profiles = "test")
public class PaymentInfoControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentInfoService paymentInfoService;

    @Test
    void givenProcess_whenEverythingIsOk_thenReturnTrue200() throws Exception {

        PaymentProcessDto dto = new PaymentProcessDto();
        UserPaymentInfoDto userPaymentInfoDto = new UserPaymentInfoDto();
        dto.setUserPaymentInfoDto(userPaymentInfoDto);
        dto.setProductKey("MONTH22");

        when(paymentInfoService.process(dto)).thenReturn(true);

        mockMvc.perform(post("/payment/process")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        verify(paymentInfoService, times(1)).process(dto);
    }

    @Test
    void givenProcess_whenProductKeyIsMissing_thenThrowBadRequest400() throws Exception {

        PaymentProcessDto dto = new PaymentProcessDto();
        UserPaymentInfoDto userPaymentInfoDto = new UserPaymentInfoDto();
        dto.setUserPaymentInfoDto(userPaymentInfoDto);

        mockMvc.perform(post("/payment/process")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("[Campo 'productKey' não pode estar vazio]")))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())));

        verify(paymentInfoService, times(0)).process(any());
    }

    @Test
    void givenProcess_whenUserPaymentInfoDtoIsMissing_thenThrowBadRequest400() throws Exception {

        PaymentProcessDto dto = new PaymentProcessDto();
        dto.setProductKey("MONTH22");

        mockMvc.perform(post("/payment/process")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("[Campo 'userPaymentInfoDto' deve ser informado]")))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())));

        verify(paymentInfoService, times(0)).process(any());
    }
}
