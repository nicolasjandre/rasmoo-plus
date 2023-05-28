package com.client.ws.rasmooplus.integration.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;

@ExtendWith(MockitoExtension.class)
public class WsRaspayIntegrationImplTest {

    @InjectMocks
    private WsRaspayIntegrationImpl wsRaspayIntegration;

    @Mock
    private RestTemplate restTemplate;

    private static HttpHeaders headers;

    @BeforeAll
    public static void loadHeaders() {
        headers = getHttpHeaders();
    }

    @Test
    void givenCreateCustomer_whenApiResponseIs201Created_thenReturnCustomerDto() {

        ReflectionTestUtils.setField(wsRaspayIntegration, "raspayHost", "http://localhost:8080");
        ReflectionTestUtils.setField(wsRaspayIntegration, "customerUrl", "/customer");

        CustomerDto dto = new CustomerDto();

        HttpEntity<CustomerDto> request = new HttpEntity<>(dto, headers);

        when(restTemplate.exchange("http://localhost:8080/customer", HttpMethod.POST, request,
                CustomerDto.class)).thenReturn(ResponseEntity.of(Optional.of(dto)));

        assertEquals(ResponseEntity.of(Optional.of(dto)).getBody(), wsRaspayIntegration.createCustomer(dto));

        verify(restTemplate, times(1))
                .exchange("http://localhost:8080/customer", HttpMethod.POST, request,
                        CustomerDto.class);
    }

    @Test
    void givenCreateCustomer_whenApiResponseIs400BadRequest_thenReturnNull() {

        ReflectionTestUtils.setField(wsRaspayIntegration, "raspayHost", "http://localhost:8080");
        ReflectionTestUtils.setField(wsRaspayIntegration, "customerUrl", "/customer");

        CustomerDto dto = new CustomerDto();

        HttpEntity<CustomerDto> request = new HttpEntity<>(dto, headers);

        when(restTemplate.exchange("http://localhost:8080/customer", HttpMethod.POST, request,
                CustomerDto.class)).thenReturn(ResponseEntity.badRequest().build());

        assertNull(wsRaspayIntegration.createCustomer(dto));

        verify(restTemplate, times(1))
                .exchange("http://localhost:8080/customer", HttpMethod.POST, request,
                        CustomerDto.class);
    }

    @Test
    void givenCreateCustomer_whenApiResponseGetThrows_thenThrowHttpClientErrorException() {

        ReflectionTestUtils.setField(wsRaspayIntegration, "raspayHost", "http://localhost:8080");
        ReflectionTestUtils.setField(wsRaspayIntegration, "customerUrl", "/customer");

        CustomerDto dto = new CustomerDto();

        HttpEntity<CustomerDto> request = new HttpEntity<>(dto, headers);

        when(restTemplate.exchange("http://localhost:8080/customer", HttpMethod.POST, request,
                CustomerDto.class)).thenThrow(RuntimeException.class);

        assertThrows(HttpClientErrorException.class, () -> wsRaspayIntegration.createCustomer(dto));

        verify(restTemplate, times(1))
                .exchange("http://localhost:8080/customer", HttpMethod.POST, request,
                        CustomerDto.class);
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders header = new HttpHeaders();

        String credential = "rasmooplus:r@sm00";
        String base64 = Base64.encodeBase64String(credential.getBytes());
        header.add("Authorization", "Basic " + base64);

        return header;
    }
}
