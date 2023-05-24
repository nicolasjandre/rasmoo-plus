package com.client.ws.rasmooplus.domain.service;

import java.util.List;

import com.client.ws.rasmooplus.domain.model.SubscriptionType;
import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;

public interface SubscriptionTypeService {

    List<SubscriptionType> findAll();

    SubscriptionType findById(Long id);

    SubscriptionType create(SubscriptionTypeDto dto);

    SubscriptionType update(Long id, SubscriptionTypeDto dto);

    void delete(Long id);
}