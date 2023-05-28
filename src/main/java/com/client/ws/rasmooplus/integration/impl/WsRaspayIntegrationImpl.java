package com.client.ws.rasmooplus.integration.impl;

import java.nio.charset.StandardCharsets;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.client.ws.rasmooplus.dto.error.ErrorResponseDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.integration.WsRaspayIntegration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WsRaspayIntegrationImpl implements WsRaspayIntegration {

    @Value("${webservices.raspay.host}")
    private String raspayHost;

    @Value("${webservices.raspay.v1.customer}")
    private String customerUrl;

    @Value("${webservices.raspay.v1.order}")
    private String orderUrl;

    @Value("${webservices.raspay.v1.payment}")
    private String paymentUrl;

    @Autowired
    private RestTemplate restTemplate;

    private final HttpHeaders headers;

    public WsRaspayIntegrationImpl() {
        this.headers = getHttpHeaders();
    }

    @Override
    public CustomerDto createCustomer(CustomerDto dto) {
        try {
            HttpEntity<CustomerDto> request = new HttpEntity<>(dto, this.headers);

            ResponseEntity<CustomerDto> response = restTemplate.exchange(
                    raspayHost + customerUrl, HttpMethod.POST, request,
                    CustomerDto.class);
            return response.getBody();
        } catch (Exception ex) {
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build();

            throw new HttpClientErrorException(ex.getMessage(), HttpStatusCode.valueOf(500),
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), headers, convertToBytes(errorResponseDto),
                    StandardCharsets.UTF_8);
        }
    }

    @Override
    public OrderDto createOrder(OrderDto dto) {
        try {
            HttpEntity<OrderDto> request = new HttpEntity<>(dto, this.headers);

            ResponseEntity<OrderDto> response = restTemplate.exchange(
                    raspayHost + orderUrl, HttpMethod.POST, request,
                    OrderDto.class);
            return response.getBody();
        } catch (Exception ex) {
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build();

            throw new HttpClientErrorException(ex.getMessage(), HttpStatusCode.valueOf(500),
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), headers, convertToBytes(errorResponseDto),
                    StandardCharsets.UTF_8);
        }
    }

    @Override
    public Boolean paymentProcess(PaymentDto dto) {
        try {
            HttpEntity<PaymentDto> request = new HttpEntity<>(dto, this.headers);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    raspayHost + paymentUrl, HttpMethod.POST, request,
                    Boolean.class);
            return response.getBody();
        } catch (Exception ex) {
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build();

            throw new HttpClientErrorException(ex.getMessage(), HttpStatusCode.valueOf(500),
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), headers, convertToBytes(errorResponseDto),
                    StandardCharsets.UTF_8);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders header = new HttpHeaders();

        String credential = "rasmooplus:r@sm00";
        String base64 = Base64.encodeBase64String(credential.getBytes());
        header.add("Authorization", "Basic " + base64);

        return header;
    }

    private static byte[] convertToBytes(ErrorResponseDto errorResponseDto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsBytes(errorResponseDto);
        } catch (Exception ex) {
            return new byte[0];
        }
    }
}
