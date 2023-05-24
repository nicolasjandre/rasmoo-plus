package com.client.ws.rasmooplus.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPaymentInfoDto {

    private Long id;

    @Size(min = 16, max = 16, message = "Campo 'cardNumber' deve conter 16 caracteres")
    private String cardNumber;

    @Min(value = 1, message = "Campo 'cardExpirationMoth' deve possuir um mês válido")
    @Max(value = 12, message = "Campo 'cardExpirationMoth' deve possuir um mês válido")
    private Long cardExpirationMonth;

    private Long cardExpirationYear;

    @Size(min = 3, max = 3, message = "Campo 'cardSecurityCode' deve conter 3 caracteres")
    private String cardSecurityCode;

    private Long installments;

    private BigDecimal price;

    private LocalDate dtPayment = LocalDate.now();

    @NotNull(message = "Campo 'userId' deve ser informado")
    private Long userId;
}
