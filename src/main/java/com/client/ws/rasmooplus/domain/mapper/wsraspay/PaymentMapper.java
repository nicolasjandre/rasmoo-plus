package com.client.ws.rasmooplus.domain.mapper.wsraspay;

import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentDto build(String customerId, String orderId, CreditCardDto creditCardDto) {

        return PaymentDto.builder()
                .creditCard(creditCardDto)
                .customerId(customerId)
                .orderId(orderId)
                .build();
    }
}
