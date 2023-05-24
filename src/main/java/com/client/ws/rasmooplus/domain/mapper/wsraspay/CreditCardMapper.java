package com.client.ws.rasmooplus.domain.mapper.wsraspay;

import com.client.ws.rasmooplus.dto.UserPaymentInfoDto;
import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;

public class CreditCardMapper {

    private CreditCardMapper() {
    }

    public static CreditCardDto build(UserPaymentInfoDto dto, String documentNumber) {

        return CreditCardDto.builder()
                .documentNumber(documentNumber)
                .cvv(Long.parseLong(dto.getCardSecurityCode()))
                .installments(dto.getInstallments())
                .month(dto.getCardExpirationMonth())
                .number(dto.getCardNumber())
                .year(dto.getCardExpirationYear())
                .build();
    }
}
