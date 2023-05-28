package com.client.ws.rasmooplus.domain.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.client.ws.rasmooplus.domain.enums.UserTypeEnum;
import com.client.ws.rasmooplus.domain.exception.BusinessException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.mapper.UserPaymentInfoMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.CreditCardMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.CustomerMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.OrderMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.PaymentMapper;
import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.domain.model.jpa.UserCredentials;
import com.client.ws.rasmooplus.domain.model.jpa.UserPaymentInfo;
import com.client.ws.rasmooplus.domain.model.jpa.UserType;
import com.client.ws.rasmooplus.domain.repository.jpa.UserCredentialsRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserPaymentInfoRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserTypeRepository;
import com.client.ws.rasmooplus.domain.service.PaymentInfoService;
import com.client.ws.rasmooplus.dto.EmailDto;
import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.integration.MailIntegration;
import com.client.ws.rasmooplus.integration.WsRaspayIntegration;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {

    @Value("${webservices.rasplus.default.password}")
    private String defaultPassword;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private UserPaymentInfoRepository userPaymentInfoRepository;

    @Autowired
    private WsRaspayIntegration wsRaspayIntegration;

    @Autowired
    private MailIntegration mailIntegration;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Override
    public Boolean process(PaymentProcessDto dto) {

        // verify if the user already has an active subscription
        Long userId = dto.getUserPaymentInfoDto().getUserId();
        var userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException("Usuário de id=[" + userId + "] não encontrado");
        }

        User user = userOpt.get();

        if (Objects.nonNull(user.getSubscriptionType())) {
            throw new BusinessException(
                    "Pagamento não pode ser processado pois o usuário já possui uma assinatura ativa");
        }

        // creates or update the user in our integration (raspay payment gateway)
        CustomerDto customerDto = wsRaspayIntegration.createCustomer(CustomerMapper.build(user));

        // creates the payment order
        OrderDto orderDto = wsRaspayIntegration.createOrder(OrderMapper.build(customerDto.getId(), dto));

        // process the payment
        PaymentDto paymentDto = PaymentMapper.build(customerDto.getId(), orderDto.getId(),
                CreditCardMapper.build(dto.getUserPaymentInfoDto(), user.getCpf()));

        boolean paymentSuccessfull = wsRaspayIntegration.paymentProcess(paymentDto);

        if (paymentSuccessfull) {
            // save the payment information
            UserPaymentInfo userPaymentInfo = UserPaymentInfoMapper.fromDtoToEntity(dto.getUserPaymentInfoDto(), user);
            userPaymentInfoRepository.save(userPaymentInfo);

            Optional<UserType> userTypeOpt = userTypeRepository.findById(UserTypeEnum.ALUNO.getId());

            if (userTypeOpt.isEmpty()) {
                throw new NotFoundException("UserType de nome " + UserTypeEnum.ALUNO + " não encontrado");
            }

            UserCredentials userCredentials = new UserCredentials(null, user.getEmail(),
                    passwordEncoder.encode(defaultPassword),
                    userTypeOpt.get());
            userCredentialsRepository.save(userCredentials);

            String message = String.format("""
                    Seja bem vindo!
                    Nós da Rasmoo desejamos sucesso nessa sua caminhada, tenha um excelente estudo!

                    Usuário: %s
                    Senha: %s
                    """, user.getEmail(), defaultPassword);

            EmailDto emailDto = new EmailDto(user.getEmail(), "Acesso liberado!", message);

            mailIntegration.send(emailDto);

            userService.updateUserSubscriptionType(user, dto.getProductKey());
        }

        return paymentSuccessfull;
    }
}
