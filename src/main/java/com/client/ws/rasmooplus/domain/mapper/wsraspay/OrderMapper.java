package com.client.ws.rasmooplus.domain.mapper.wsraspay;

import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto build(String customerId, PaymentProcessDto paymentProcessDto) {
        return OrderDto.builder()
                .customerId(customerId)
                .discount(paymentProcessDto.getDiscount())
                .productAcronym(paymentProcessDto.getProductKey())
                .build();
    }
}
