package com.client.ws.rasmooplus.integration;

public interface MailIntegration {

    void send(String mailTo, String subject, String message);
}
