package com.client.ws.rasmooplus.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessDto {

    @NotBlank(message = "Campo 'productKey' não pode estar vazio")
    private String productKey;

    private BigDecimal discount;

    @NotNull(message = "Campo 'userPaymentInfoDto' deve ser informado")
    private UserPaymentInfoDto userPaymentInfoDto;
}
