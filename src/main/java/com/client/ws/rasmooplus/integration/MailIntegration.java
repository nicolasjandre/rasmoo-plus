package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.dto.EmailDto;

public interface MailIntegration {

    void send(EmailDto emailDto);
}
