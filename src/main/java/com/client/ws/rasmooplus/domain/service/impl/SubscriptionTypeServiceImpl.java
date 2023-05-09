package com.client.ws.rasmooplus.domain.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.ws.rasmooplus.domain.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.domain.exception.BadRequestException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.model.SubscriptionType;
import com.client.ws.rasmooplus.domain.repository.SubscriptionTypeRepository;
import com.client.ws.rasmooplus.domain.service.SubscriptionTypeService;

@Service
public class SubscriptionTypeServiceImpl implements SubscriptionTypeService {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Override
    public SubscriptionType create(SubscriptionTypeDto dto) {
        if (Objects.nonNull(dto.getId())) {
            throw new BadRequestException("O id deve ser nulo.");
        }

        return subscriptionTypeRepository.save(
                SubscriptionType.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .accessMonths(dto.getAccessMonth())
                        .price(dto.getPrice())
                        .productKey(dto.getProductKey())
                        .build());
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        subscriptionTypeRepository.deleteById(id);
    }

    @Override
    public List<SubscriptionType> findAll() {
        return subscriptionTypeRepository.findAll();
    }

    @Override
    public SubscriptionType findById(Long id) {
        Optional<SubscriptionType> optionalSubscriptionType = subscriptionTypeRepository.findById(id);

        if (optionalSubscriptionType.isEmpty()) {
            throw new NotFoundException("SubscriptionType id = [" + id + "] não encontrado.");
        }

        return optionalSubscriptionType.get();
    }

    @Override
    public SubscriptionType update(Long id, SubscriptionTypeDto dto) {
        this.findById(id);

        return subscriptionTypeRepository.save(
                SubscriptionType.builder()
                        .id(id)
                        .name(dto.getName())
                        .accessMonths(dto.getAccessMonth())
                        .price(dto.getPrice())
                        .productKey(dto.getProductKey())
                        .build());
    }
}
