package com.client.ws.rasmooplus.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.client.ws.rasmooplus.controller.SubscriptionTypeController;
import com.client.ws.rasmooplus.domain.exception.BadRequestException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.mapper.SubscriptionTypeMapper;
import com.client.ws.rasmooplus.domain.model.jpa.SubscriptionType;
import com.client.ws.rasmooplus.domain.repository.jpa.SubscriptionTypeRepository;
import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;

@ExtendWith(MockitoExtension.class)
public class SubscriptionTypeServiceTest {

    @InjectMocks
    private SubscriptionTypeServiceImpl subscriptionTypeService;

    @Mock
    private SubscriptionTypeRepository subscriptionTypeRepository;

    private SubscriptionTypeDto dto;

    @BeforeEach
    public void loadSubscriptionType() {
        dto = new SubscriptionTypeDto();
        dto.setProductKey("MONTH22");
        dto.setName("Plano mensal");
        dto.setAccessMonths(1L);
        dto.setPrice(BigDecimal.valueOf(69.90));
    }

    @Test
    void givenCreate_whenSubscriptionTypeDtoHasId_thenThrowBadRequestException() {

        dto.setId(1L);

        assertEquals("Não pode haver um id no body",
                assertThrows(BadRequestException.class, () -> subscriptionTypeService.create(dto))
                        .getMessage());

        verify(subscriptionTypeRepository, times(0)).save(any());
    }

    @Test
    void givenCreate_whenSubscriptionTypeDtoHasNoId_thenSaveAndReturnSubscriptionType() {

        SubscriptionType subscriptionType = SubscriptionTypeMapper.fromDtoToEntity(dto);

        when(subscriptionTypeRepository.save(subscriptionType)).thenReturn(subscriptionType);

        assertEquals(subscriptionType, subscriptionTypeService.create(dto));

        verify(subscriptionTypeRepository, times(1)).save(subscriptionType);
    }

    @Test
    void givenFindById_whenSubscriptionTypeNotFound_thenThrowNotFoundException() {

        Long id = 1L;

        when(subscriptionTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("SubscriptionType id=[" + id + "] não encontrado",
                assertThrows(NotFoundException.class,
                        () -> subscriptionTypeService.findById(id))
                        .getMessage());

        verify(subscriptionTypeRepository, times(1)).findById(id);
    }

    @Test
    void givenFindById_whenSubscriptionTypeIsFound_thenSaveAndReturnSubscriptionType() {

        Long id = 1L;

        SubscriptionType subscriptionType = SubscriptionTypeMapper.fromDtoToEntity(dto);

        when(subscriptionTypeRepository.findById(1L)).thenReturn(Optional.of(subscriptionType));

        assertEquals(subscriptionType.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(SubscriptionTypeController.class).findById(id)).withSelfRel()),
                subscriptionTypeService.findById(id));

        verify(subscriptionTypeRepository, times(1)).findById(id);
    }

    @Test
    void givenDelete_whenSubscriptionTypeIsFound_thenSaveAndReturnVoid() {

        Long id = 1L;

        SubscriptionType subscriptionType = SubscriptionTypeMapper.fromDtoToEntity(dto);

        when(subscriptionTypeRepository.findById(1L)).thenReturn(Optional.of(subscriptionType));

        assertDoesNotThrow(() -> subscriptionTypeService.delete(id));

        verify(subscriptionTypeRepository, times(1)).findById(id);
        verify(subscriptionTypeRepository, times(1)).deleteById(id);
    }

    @Test
    void givenDelete_whenSubscriptionTypeIsNotFound_thenThrowNotFoundException() {

        Long id = 1L;

        when(subscriptionTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("SubscriptionType id=[" + id + "] não encontrado",
                assertThrows(NotFoundException.class,
                        () -> subscriptionTypeService.delete(id))
                        .getMessage());

        verify(subscriptionTypeRepository, times(1)).findById(id);
        verify(subscriptionTypeRepository, times(0)).deleteById(any());
    }

    @Test
    void givenFindAll_whenThereIsNoDataInDb_thenReturnAnEmptyList() {

        List<SubscriptionType> subscriptionTypeList = new ArrayList<>();

        when(subscriptionTypeService.findAll()).thenReturn(subscriptionTypeList);

        assertEquals(0, subscriptionTypeRepository.findAll().size());

        verify(subscriptionTypeRepository, times(1)).findAll();
    }

    @Test
    void givenFindAll_whenThereIsDataOnDb_thenReturnAFilledList() {

        List<SubscriptionType> subscriptionTypeList = new ArrayList<>();
        SubscriptionType subscriptionType = SubscriptionTypeMapper.fromDtoToEntity(dto);
        subscriptionTypeList.add(subscriptionType);

        when(subscriptionTypeService.findAll()).thenReturn(subscriptionTypeList);

        assertEquals(1, subscriptionTypeRepository.findAll().size());

        verify(subscriptionTypeRepository, times(1)).findAll();
    }

    @Test
    void givenUpdate_whenSubscriptionTypeIsNotFound_thenThrowNotFoundException() {

        Long id = 1L;

        when(subscriptionTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("SubscriptionType id=[" + id + "] não encontrado",
                assertThrows(NotFoundException.class,
                        () -> subscriptionTypeService.update(id, dto))
                        .getMessage());

        verify(subscriptionTypeRepository, times(1)).findById(id);
        verify(subscriptionTypeRepository, times(0)).save(any());
    }

    @Test
    void givenUpdate_whenSubscriptionTypeIsFound_thenUpdateAndReturnUpdatedSubscriptionType() {

        Long id = 1L;

        SubscriptionType subscriptionType= SubscriptionTypeMapper.fromDtoToEntity(dto);

        when(subscriptionTypeRepository.findById(1L)).thenReturn(Optional.of(subscriptionType));

        subscriptionType.setId(id);

        when(subscriptionTypeRepository.save(subscriptionType)).thenReturn(subscriptionType);

        assertEquals(subscriptionType, subscriptionTypeService.update(id, dto));

        verify(subscriptionTypeRepository, times(1)).findById(id);
        verify(subscriptionTypeRepository, times(1)).save(subscriptionType);
    }
}
