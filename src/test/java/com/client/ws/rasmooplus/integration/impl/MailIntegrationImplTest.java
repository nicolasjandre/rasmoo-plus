package com.client.ws.rasmooplus.integration.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.client.ws.rasmooplus.dto.EmailDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
public class MailIntegrationImplTest {

    @InjectMocks
    private MailIntegrationImpl mailIntegration;

    @Mock
    private JavaMailSender javaMailSender;

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void givenSend_whenAllArgsReceiveString_thenDontThrowAnyErrorAndReturnVoid() {

        EmailDto emailDto = new EmailDto();
        emailDto.setMailTo("teste@teste.com");
        emailDto.setSubject("Teste");
        emailDto.setMessage("Mensagem de teste");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailDto.getMailTo());
        simpleMailMessage.setSubject(emailDto.getSubject());
        simpleMailMessage.setText(emailDto.getMessage());

        assertDoesNotThrow(() -> mailIntegration.send(emailDto));
         
        verify(javaMailSender, times(1)).send(simpleMailMessage);
    }

    @ParameterizedTest
    @CsvSource({
            "teste.com, Teste, Mensagem de teste",
            "teste@teste.com, , Mensagem de teste",
            "teste@teste.com, Teste, "
    })
    void givenSend_whenInvalidEmailDtoFields_thenThrowMethodArgumentNotValidException(
            String mailTo, String subject, String message) {

        // Arrange
        EmailDto emailDto = new EmailDto();
        emailDto.setMailTo(mailTo);
        emailDto.setSubject(subject);
        emailDto.setMessage(message);

        Set<ConstraintViolation<EmailDto>> violations = validator.validate(emailDto);

        assertEquals(1, violations.size());
    }
}
