package com.client.ws.rasmooplus.integration.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.client.ws.rasmooplus.dto.EmailDto;
import com.client.ws.rasmooplus.integration.MailIntegration;

import jakarta.validation.Valid;

@Component
public class MailIntegrationImpl implements MailIntegration {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void send(@Valid EmailDto emailDto) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailDto.getMailTo());
        simpleMailMessage.setSubject(emailDto.getSubject());
        simpleMailMessage.setText(emailDto.getMessage());
        javaMailSender.send(simpleMailMessage);
    }
}
