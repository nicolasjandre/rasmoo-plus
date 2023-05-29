package com.client.ws.rasmooplus.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

import com.client.ws.rasmooplus.domain.mapper.SubscriptionTypeMapper;
import com.client.ws.rasmooplus.domain.model.jpa.SubscriptionType;
import com.client.ws.rasmooplus.domain.service.SubscriptionTypeService;
import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@WebMvcTest(SubscriptionTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(profiles = "test")
public class SubscriptionTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionTypeService subscriptionTypeService;

    @Test
    void givenFindAll_thenReturnAllSubscriptionType200() throws Exception {
        List<SubscriptionType> SubscriptionTypeList = new ArrayList<>();

        SubscriptionType subscriptionType1 = new SubscriptionType(1L, "Plano Mensal", 1L, BigDecimal.valueOf(69.9),
                "MONTH22");
        SubscriptionType subscriptionType2 = new SubscriptionType(2L, "Plano Trimestral", 1L, BigDecimal.valueOf(149.9),
                "TRI222");
        SubscriptionTypeList.add(subscriptionType1);
        SubscriptionTypeList.add(subscriptionType2);

        when(subscriptionTypeService.findAll()).thenReturn(SubscriptionTypeList);

        mockMvc.perform(get("/subscription-type"))
                .andExpect(status().is(HttpStatus.OK.value()));

        verify(subscriptionTypeService, times(1)).findAll();
    }

    @Test
    void givenFindById_whenReceivesId2_thenReturnId2SubscriptionType200() throws Exception {

        SubscriptionType subscriptionType = new SubscriptionType(2L, "Plano Mensal", 1L, BigDecimal.valueOf(69.9),
                "MONTH22");

        when(subscriptionTypeService.findById(2L)).thenReturn(subscriptionType);

        mockMvc.perform(get("/subscription-type/2"))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(status().is(HttpStatus.OK.value()));

        verify(subscriptionTypeService, times(1)).findById(2L);

    }

    @Test
    void givenDelete_whenReceivesId1_thenReturnVoid204() throws Exception {

        doNothing().when(subscriptionTypeService).delete(1L);

        mockMvc.perform(delete("/subscription-type/1"))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        verify(subscriptionTypeService, times(1)).delete(1L);
    }

    @Test
    void givenUpdate_whenReceivesValidId_thenReturnUpdatedSubscriptionType201() throws Exception {

        SubscriptionTypeDto subscriptionTypeDto = new SubscriptionTypeDto(1L, "Plano Mensal", 1L,
                BigDecimal.valueOf(69.9), "MONTH23");

        when(subscriptionTypeService.update(1L, subscriptionTypeDto))
                .thenReturn(SubscriptionTypeMapper.fromDtoToEntity(subscriptionTypeDto));

        mockMvc.perform(put("/subscription-type/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(subscriptionTypeDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        verify(subscriptionTypeService, times(1)).update(1L, subscriptionTypeDto);
    }

    @Test
    void givenUpdate_whenDtoMissingValues_thenThrowBadRequest400() throws Exception {

        SubscriptionTypeDto subscriptionTypeDto = new SubscriptionTypeDto(1L, "Plano Mensal", 1L,
                BigDecimal.valueOf(69.9), "");

        mockMvc.perform(put("/subscription-type/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(subscriptionTypeDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        verify(subscriptionTypeService, times(0)).update(any(), any());
    }

    @Test
    void givenUpdate_whenDtoIdIsNull_thenThrowNotFound404() throws Exception {

        SubscriptionTypeDto subscriptionTypeDto = new SubscriptionTypeDto(null, "Plano Mensal", 1L,
                BigDecimal.valueOf(69.9), "");

        mockMvc.perform(put("/subscription-type/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(subscriptionTypeDto)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        verify(subscriptionTypeService, times(0)).update(any(), any());
    }

    @Test
    void givenCreate_whenReceivesValidDto_thenReturnCreatedSubscriptionType201() throws Exception {

        SubscriptionType subscriptionType = new SubscriptionType(1L, "Plano Mensal", 1L, BigDecimal.valueOf(69.9),
                "MONTH23");

        SubscriptionTypeDto subscriptionTypeDto = new SubscriptionTypeDto(null, "Plano Mensal", 1L,
                BigDecimal.valueOf(69.9), "MONTH23");

        when(subscriptionTypeService.create(subscriptionTypeDto))
                .thenReturn(subscriptionType);

        mockMvc.perform(post("/subscription-type")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(subscriptionTypeDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        verify(subscriptionTypeService, times(1)).create(subscriptionTypeDto);
    }

    @Test
    void givenCreate_whenDtoHasMissingValues_thenThrowsBadRequest400() throws Exception {

        SubscriptionTypeDto subscriptionTypeDto = new SubscriptionTypeDto(1L, "OI", 1L,
                BigDecimal.valueOf(69.9), "MONTH23");

        mockMvc.perform(post("/subscription-type")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(subscriptionTypeDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message",
                        is("[Campo 'name' deve ter entre 5 e 30 caracteres]")))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        verify(subscriptionTypeService, times(0)).create(any());
    }
}
