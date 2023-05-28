package com.client.ws.rasmooplus.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.client.ws.rasmooplus.domain.exception.BadRequestException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.model.jpa.UserCredentials;
import com.client.ws.rasmooplus.domain.model.redis.UserRecoveryCode;
import com.client.ws.rasmooplus.domain.repository.jpa.UserCredentialsRepository;
import com.client.ws.rasmooplus.domain.repository.redis.UserRecoveryCodeRepository;
import com.client.ws.rasmooplus.dto.EmailDto;
import com.client.ws.rasmooplus.dto.UserCredentialsDto;
import com.client.ws.rasmooplus.integration.MailIntegration;

@ExtendWith(MockitoExtension.class)
public class UserCredentialsServiceTest {

    @InjectMocks
    private UserCredentialsServiceImpl userCredentialsService;

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private UserRecoveryCodeRepository userRecoveryCodeRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private MailIntegration mailIntegration;

    private final String email = "teste@teste.com";

    private final Long recoveryCodeTimeout = 5L;

    @Test
    void givenLoadUserByUsername_whenUserIsNotFound_thenThrowNotFoundException() {

        when(userCredentialsRepository.findByUsername(email)).thenReturn(Optional.empty());

        assertEquals("Usuário ou senha inválidos", 
                assertThrows(NotFoundException.class, 
                () -> userCredentialsService.loadUserByUsername(email))
                .getMessage());

        verify(userCredentialsRepository, times(1)).findByUsername(email);
    }

    @Test
    void givenLoadUserByUsername_whenUserCredentialsIsFound_thenReturnUserCredentials() {

        UserCredentials userCredentials = new UserCredentials();

        when(userCredentialsRepository.findByUsername(email)).thenReturn(Optional.of(userCredentials));

        assertEquals(userCredentials, userCredentialsService.loadUserByUsername(email));

        verify(userCredentialsRepository, times(1)).findByUsername(email);
    }

    @Test
    void givenSendRecoveryCode_whenUserRecoveryCodeAndUserCredentialsIsNotFound_thenThrowNotFoundException() {

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userCredentialsRepository.findByUsername(email)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado", 
                assertThrows(NotFoundException.class, 
                () -> userCredentialsService.sendRecoveryCode(email))
                .getMessage());

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
        verify(userCredentialsRepository, times(1)).findByUsername(email);
        verify(userRecoveryCodeRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
    }

    @Test
    void givenSendRecoveryCode_whenUserRecoveryCodeIsNotFoundAndUserCredentialsIsFound_thenDontThrowAnyErrorSaveUserRecoveryCodeAndJustReturnVoid() {

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername(email);

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userCredentialsRepository.findByUsername(email)).thenReturn(Optional.of(userCredentials));

        ArgumentCaptor<UserRecoveryCode> userRecoveryCodeCaptor = ArgumentCaptor.forClass(UserRecoveryCode.class);

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode();
        userRecoveryCode.setEmail(userCredentials.getUsername());

        doAnswer(invocation -> {
            UserRecoveryCode savedUserRecoveryCode = invocation.getArgument(0);
            String code = savedUserRecoveryCode.getCode();
            LocalDateTime creationDate = savedUserRecoveryCode.getCreationDate();
            userRecoveryCode.setCode(code);
            userRecoveryCode.setCreationDate(creationDate);
            return null;
        }).when(userRecoveryCodeRepository).save(userRecoveryCodeCaptor.capture());

        assertDoesNotThrow(() -> userCredentialsService.sendRecoveryCode(email));

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
        verify(userCredentialsRepository, times(1)).findByUsername(email);
        verify(userRecoveryCodeRepository, times(1)).save(userRecoveryCode);
        verify(mailIntegration, times(1)).send(any(EmailDto.class));
    }

    @Test
    void givenSendRecoveryCode_whenUserRecoveryCodeAndUserCredentialsAreFound_thenDontThrowAnyErrorSaveUserRecoveryCodeAndJustReturnVoid() {

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode();
        userRecoveryCode.setEmail(email);

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        ArgumentCaptor<UserRecoveryCode> userRecoveryCodeCaptor = ArgumentCaptor.forClass(UserRecoveryCode.class);

        doAnswer(invocation -> {
            UserRecoveryCode savedUserRecoveryCode = invocation.getArgument(0);
            String code = savedUserRecoveryCode.getCode();
            LocalDateTime creationDate = savedUserRecoveryCode.getCreationDate();
            userRecoveryCode.setCode(code);
            userRecoveryCode.setCreationDate(creationDate);
            return null;
        }).when(userRecoveryCodeRepository).save(userRecoveryCodeCaptor.capture());

        assertDoesNotThrow(() -> userCredentialsService.sendRecoveryCode(email));

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
        verify(userCredentialsRepository, times(0)).findByUsername(any());
        verify(userRecoveryCodeRepository, times(1)).save(userRecoveryCode);
        verify(mailIntegration, times(1)).send(any(EmailDto.class));
    }

    @Test
    void givenIsRecoveryCodeValid_whenUserRecoveryCodeIsNotFound_thenThrowNotFoundException() {

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado", 
                assertThrows(NotFoundException.class, 
                () -> userCredentialsService.isRecoveryCodeValid("1234", email))
                .getMessage());

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenIsRecoveryCodeValid_whenUserRecoveryCodeIsFoundNotExpiredNotUsedAndMatch_thenReturnTrue() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "1234", false, LocalDateTime.now());

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        assertTrue(userCredentialsService.isRecoveryCodeValid("1234", email));

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenIsRecoveryCodeValid_whenUserRecoveryCodeIsFoundNotExpiredNotUsedButNotMatch_thenReturnTrue() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "4321", false, LocalDateTime.now());

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        assertFalse(userCredentialsService.isRecoveryCodeValid("1234", email));

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenIsRecoveryCodeValid_whenUserRecoveryCodeIsFoundNotUsedMatchButIsExpired_thenReturnTrue() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "1234", false, LocalDateTime.now().minusMinutes(recoveryCodeTimeout));

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        assertFalse(userCredentialsService.isRecoveryCodeValid("1234", email));

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenIsRecoveryCodeValid_whenUserRecoveryCodeIsFoundNotExpiredMatchButAreAlreadyUsed_thenReturnTrue() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "1234", true, LocalDateTime.now());

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        assertFalse(userCredentialsService.isRecoveryCodeValid("1234", email));

        verify(userRecoveryCodeRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenUpdatePasswordByRecoveryCode_whenPasswordAndConfirmationDontMatch_thenThrowBadRequestException() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserCredentialsDto dto = new UserCredentialsDto(email, "1234", "123");

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "4321", false, LocalDateTime.now());

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        assertEquals("A senha e a confirmação de senha precisam ser idênticas",
                assertThrows(BadRequestException.class,
                        () -> userCredentialsService.updatePasswordByRecoveryCode(dto, "9999"))
                        .getMessage());

        verify(userCredentialsRepository, times(0)).findByUsername(anyString());
        verify(userCredentialsRepository, times(0)).save(any());
        verify(userRecoveryCodeRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
    }

    @Test
    void givenUpdatePasswordByRecoveryCode_whenPasswordMatchButRecoveryCodeIsInvalid_thenThrowBadRequestException() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserCredentialsDto dto = new UserCredentialsDto(email, "1234", "1234");

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "4321", true, LocalDateTime.now().minusMinutes(5));

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        assertDoesNotThrow(() -> userCredentialsService.updatePasswordByRecoveryCode(dto, "9999"));

        verify(userCredentialsRepository, times(0)).findByUsername(anyString());
        verify(userCredentialsRepository, times(0)).save(any());
        verify(userRecoveryCodeRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
    }

    @Test
    void givenUpdatePasswordByRecoveryCode_whenPasswordMatchAndRecoveryCodeIsValid_thenUpdateUserCredentialsUpdateUserRecoveryCodeAndSendEmail() {

        ReflectionTestUtils.setField(userCredentialsService, "recoveryCodeTimeout", recoveryCodeTimeout);

        UserCredentialsDto dto = new UserCredentialsDto(email, "1234", "1234");

        UserRecoveryCode userRecoveryCode = new UserRecoveryCode("RANDOMID",
                email, "4321", false, LocalDateTime.now());

        when(userRecoveryCodeRepository.findByEmail(email)).thenReturn(Optional.of(userRecoveryCode));

        UserCredentials userCredentials = new UserCredentials(1L, email, "cryptedPass", null);

        when(userCredentialsRepository.findByUsername(email)).thenReturn(Optional.of(userCredentials));

        assertDoesNotThrow(() -> userCredentialsService.updatePasswordByRecoveryCode(dto, "4321"));

        verify(userCredentialsRepository, times(1)).findByUsername(email);
        verify(userCredentialsRepository, times(1)).save(userCredentials);
        verify(userRecoveryCodeRepository, times(1)).save(userRecoveryCode);
        verify(mailIntegration, times(1)).send(any(EmailDto.class));
    }
}
