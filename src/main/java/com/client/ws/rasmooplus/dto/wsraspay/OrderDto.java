package com.client.ws.rasmooplus.dto.wsraspay;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private String id;

    private String customerId;

    private BigDecimal discount;

    private String productAcronym;
}
