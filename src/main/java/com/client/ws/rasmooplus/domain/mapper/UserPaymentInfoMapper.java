package com.client.ws.rasmooplus.domain.mapper;

import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.domain.model.jpa.UserPaymentInfo;
import com.client.ws.rasmooplus.dto.UserPaymentInfoDto;

public class UserPaymentInfoMapper {

    private UserPaymentInfoMapper() {
    }

    public static UserPaymentInfo fromDtoToEntity(UserPaymentInfoDto dto, User user) {

        return UserPaymentInfo.builder()
                .cardNumber(dto.getCardNumber())
                .cardExpirationMonth(dto.getCardExpirationMonth())
                .cardExpirationYear(dto.getCardExpirationYear())
                .cardSecurityCode(dto.getCardSecurityCode())
                .price(dto.getPrice())
                .installments(dto.getInstallments())
                .dtPayment(dto.getDtPayment())
                .user(user)
                .build();
    }
}
