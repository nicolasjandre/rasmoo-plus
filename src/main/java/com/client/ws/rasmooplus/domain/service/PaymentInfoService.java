package com.client.ws.rasmooplus.domain.service;

import com.client.ws.rasmooplus.dto.PaymentProcessDto;

public interface PaymentInfoService {
    
    Boolean process(PaymentProcessDto dto);
}
