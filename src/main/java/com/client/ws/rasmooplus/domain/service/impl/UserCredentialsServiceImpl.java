package com.client.ws.rasmooplus.domain.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.client.ws.rasmooplus.domain.exception.BadRequestException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.model.jpa.UserCredentials;
import com.client.ws.rasmooplus.domain.model.redis.UserRecoveryCode;
import com.client.ws.rasmooplus.domain.repository.jpa.UserCredentialsRepository;
import com.client.ws.rasmooplus.domain.repository.redis.UserRecoveryCodeRepository;
import com.client.ws.rasmooplus.dto.EmailDto;
import com.client.ws.rasmooplus.dto.UserCredentialsDto;
import com.client.ws.rasmooplus.integration.MailIntegration;

@Component
public class UserCredentialsServiceImpl implements UserDetailsService {

    @Value("${webservices.rasplus.recoverycode.timeout}")
    private Long recoveryCodeTimeout;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private UserRecoveryCodeRepository userRecoveryCodeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MailIntegration mailIntegration;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserCredentials> userCredentialsOpt = userCredentialsRepository.findByUsername(username);

        if (userCredentialsOpt.isEmpty()) {
            throw new NotFoundException("Usuário ou senha inválidos");
        }

        return userCredentialsOpt.get();
    }

    public void sendRecoveryCode(String email) {

        UserRecoveryCode userRecoveryCode;

        String code = String.format("%04d", new Random().nextInt(10000));

        var userRecoveryCodeOpt = userRecoveryCodeRepository.findByEmail(email);

        if (userRecoveryCodeOpt.isEmpty()) {

            var userOpt = userCredentialsRepository.findByUsername(email);

            if (userOpt.isEmpty()) {
                throw new NotFoundException("Usuário não encontrado");
            }

            userRecoveryCode = new UserRecoveryCode();
            userRecoveryCode.setEmail(email);

        } else {

            userRecoveryCode = userRecoveryCodeOpt.get();

        }

        userRecoveryCode.setCode(code);
        userRecoveryCode.setCreationDate(LocalDateTime.now());

        userRecoveryCodeRepository.save(userRecoveryCode);

        EmailDto emailDto = new EmailDto(email, "Código de recuperação", "Código de recuperação: " + code);

        mailIntegration.send(emailDto);
    }

    public boolean isRecoveryCodeValid(String recoveryCode, String email) {

        Optional<UserRecoveryCode> userRecoveryCodeOpt = userRecoveryCodeRepository.findByEmail(email);

        if (userRecoveryCodeOpt.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado");
        }

        UserRecoveryCode userRecoveryCode = userRecoveryCodeOpt.get();

        LocalDateTime expiration = userRecoveryCode.getCreationDate().plusMinutes(recoveryCodeTimeout);
        boolean isRecoveryCodeExpired = expiration.isBefore(LocalDateTime.now());
        boolean isAlreadyUsed = userRecoveryCode.isAlreadyUsed();
        boolean doRecoveryCodeMatch = recoveryCode.equals(userRecoveryCode.getCode());

        return doRecoveryCodeMatch && !isRecoveryCodeExpired && !isAlreadyUsed;
    }

    public void updatePasswordByRecoveryCode(UserCredentialsDto dto, String recoveryCode) {

        boolean isRecoveryCodeValid = this.isRecoveryCodeValid(recoveryCode, dto.getEmail());
        boolean doPasswordAndConfirmationMatch = dto.getPassword().equals(dto.getPasswordConfirmation());

        if (!doPasswordAndConfirmationMatch) {

            throw new BadRequestException("A senha e a confirmação de senha precisam ser idênticas");

        }

        if (isRecoveryCodeValid) {

            UserCredentials userCredentials = userCredentialsRepository.findByUsername(dto.getEmail()).get();

            userCredentials.setPassword(passwordEncoder.encode(dto.getPassword()));

            userCredentialsRepository.save(userCredentials);

            UserRecoveryCode userRecoveryCode = userRecoveryCodeRepository.findByEmail(dto.getEmail()).get();
            userRecoveryCode.setAlreadyUsed(true);
            userRecoveryCodeRepository.save(userRecoveryCode);

            String message = String.format(
                    """
                            Olá %s, sua senha foi alterada com sucesso!

                            Caso não tenha sido você quem requisitou esta alteração de senha, entre em contato com nosso suporte.
                            """,
                    dto.getEmail());

            EmailDto emailDto = new EmailDto(dto.getEmail(), "Alteração de senha", message);

            mailIntegration.send(emailDto);
        }
    }
}
