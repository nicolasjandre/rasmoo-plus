package com.client.ws.rasmooplus.domain.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.client.ws.rasmooplus.domain.repository.jpa.SubscriptionTypeRepository;

@ExtendWith(MockitoExtension.class)
public class SubscriptionTypeServiceTest {

    @InjectMocks
    private SubscriptionTypeServiceImpl subscriptionTypeService;

    @Mock
    private SubscriptionTypeRepository subscriptionTypeRepository;
}
