package com.client.ws.rasmooplus.integration;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;

@SpringBootTest
class WsRaspayIntegrationImplTest {

    @Autowired
    private WsRaspayIntegration wsRaspayIntegration;

    @Test
    CustomerDto createCustomerWhenDtoOK() {
        CustomerDto dto = new CustomerDto(null, "18046417742", "nicolasjandre@live.com", "Nicolas", "Jandre");
        return wsRaspayIntegration.createCustomer(dto);
    }

    @Test
    OrderDto createOrderWhenDtoOK(String id) {
        OrderDto dto = new OrderDto(null, id, BigDecimal.ZERO, "MONTH22");
        return wsRaspayIntegration.createOrder(dto);
    }

    @Test
    void processPaymentWhenDtoOK() {
        CustomerDto customerDto = this.createCustomerWhenDtoOK();
        OrderDto orderDto = this.createOrderWhenDtoOK(customerDto.getId());
        CreditCardDto creditCardDto = new CreditCardDto(123L, "18046417742",
                0L, 6L, "1579254597200324", 2025L);

        PaymentDto paymentDto = new PaymentDto(creditCardDto, customerDto.getId(), orderDto.getId());
        wsRaspayIntegration.paymentProcess(paymentDto);
    }
}
