package com.client.ws.rasmooplus.domain.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionTypeDto {
    private Long id;

    private String name;

    private Long accessMonth;

    private BigDecimal price;

    private String productKey;
}
