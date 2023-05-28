package com.client.ws.rasmooplus.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.client.ws.rasmooplus.domain.enums.UserTypeEnum;
import com.client.ws.rasmooplus.domain.exception.BusinessException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.mapper.UserPaymentInfoMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.CreditCardMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.CustomerMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.OrderMapper;
import com.client.ws.rasmooplus.domain.mapper.wsraspay.PaymentMapper;
import com.client.ws.rasmooplus.domain.model.jpa.SubscriptionType;
import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.domain.model.jpa.UserCredentials;
import com.client.ws.rasmooplus.domain.model.jpa.UserPaymentInfo;
import com.client.ws.rasmooplus.domain.model.jpa.UserType;
import com.client.ws.rasmooplus.domain.repository.jpa.UserCredentialsRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserPaymentInfoRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserTypeRepository;
import com.client.ws.rasmooplus.dto.EmailDto;
import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.dto.UserPaymentInfoDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.integration.MailIntegration;
import com.client.ws.rasmooplus.integration.WsRaspayIntegration;

@ExtendWith(MockitoExtension.class)
public class PaymentInfoServiceTest {

    @InjectMocks
    private PaymentInfoServiceImpl paymentInfoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private UserPaymentInfoRepository userPaymentInfoRepository;

    @Mock
    private WsRaspayIntegration wsRaspayIntegration;

    @Mock
    private MailIntegration mailIntegration;

    @Mock
    private UserTypeRepository userTypeRepository;

    private PaymentProcessDto paymentProcessDto;

    private UserPaymentInfoDto userPaymentInfoDto;

    private User user;

    @BeforeEach
    public void loadUserPaymentInfoDto() {
        userPaymentInfoDto = new UserPaymentInfoDto();
        userPaymentInfoDto.setId(1L);
        userPaymentInfoDto.setUserId(1L);
        userPaymentInfoDto.setCardExpirationMonth(12L);
        userPaymentInfoDto.setCardExpirationYear(2025L);
        userPaymentInfoDto.setCardNumber("4643568978465389");
        userPaymentInfoDto.setCardSecurityCode("953");
        userPaymentInfoDto.setInstallments(4L);
        userPaymentInfoDto.setPrice(BigDecimal.valueOf(69.90));
    }

    @BeforeEach
    public void loadPaymentProcessDto() {
        paymentProcessDto = new PaymentProcessDto();
        paymentProcessDto.setProductKey("MONTH22");
        paymentProcessDto.setDiscount(BigDecimal.ZERO);
        paymentProcessDto.setUserPaymentInfoDto(userPaymentInfoDto);
    }

    @BeforeEach
    public void loadUser() {
        user = new User();
        user.setCpf("13467916533");
        user.setEmail("teste@teste.com");
        user.setName("Teste");
        user.setDtExpiration(LocalDate.now());
        user.setDtSubscription(LocalDate.now());
        user.setPhone("21969559856");
        user.setUserType(new UserType());
    }

    @Test
    void givenProcess_whenUserIsNotFound_thenThrowNotFoundException() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Usuário de id=[" + userPaymentInfoDto.getUserId() + "] não encontrado", 
                assertThrows(NotFoundException.class, 
                () -> paymentInfoService.process(paymentProcessDto))
                .getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(wsRaspayIntegration, times(0)).createCustomer(any());
        verify(wsRaspayIntegration, times(0)).createOrder(any());
        verify(userPaymentInfoRepository, times(0)).save(any());
        verify(userTypeRepository, times(0)).findById(any());
        verify(userCredentialsRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
        verify(userService, times(0)).updateUserSubscriptionType(any(), any());
    }

    @Test
    void givenProcess_whenUserIsFoundButHasActiveSubscription_thenThrowBusinessException() {

        user.setSubscriptionType(new SubscriptionType());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertEquals("Pagamento não pode ser processado pois o usuário já possui uma assinatura ativa",
                assertThrows(BusinessException.class,
                        () -> paymentInfoService.process(paymentProcessDto))
                        .getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(wsRaspayIntegration, times(0)).createCustomer(any());
        verify(wsRaspayIntegration, times(0)).createOrder(any());
        verify(userPaymentInfoRepository, times(0)).save(any());
        verify(userTypeRepository, times(0)).findById(any());
        verify(userCredentialsRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
        verify(userService, times(0)).updateUserSubscriptionType(any(), any());
    }

    @Test
    void givenProcess_whenPaymentProcessFail_thenReturnFalse() {

        CustomerDto customerDto = CustomerMapper.build(user);

        OrderDto orderDto = OrderMapper.build(customerDto.getId(), paymentProcessDto);

        PaymentDto paymentDto = PaymentMapper.build(customerDto.getId(), orderDto.getId(),
                CreditCardMapper.build(paymentProcessDto.getUserPaymentInfoDto(), user.getCpf()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wsRaspayIntegration.createCustomer(customerDto)).thenReturn(customerDto);
        when(wsRaspayIntegration.createOrder(orderDto)).thenReturn(orderDto);
        when(wsRaspayIntegration.paymentProcess(paymentDto)).thenReturn(Boolean.FALSE);

        assertFalse(() -> paymentInfoService.process(paymentProcessDto));

        verify(userRepository, times(1)).findById(1L);
        verify(wsRaspayIntegration, times(1)).createCustomer(CustomerMapper.build(user));
        verify(wsRaspayIntegration, times(1)).createOrder(OrderMapper.build(customerDto.getId(), paymentProcessDto));
        verify(userPaymentInfoRepository, times(0)).save(any());
        verify(userTypeRepository, times(0)).findById(any());
        verify(userCredentialsRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
        verify(userService, times(0)).updateUserSubscriptionType(any(), any());

    }

    @Test
    void givenProcess_whenPaymentProcessIsSuccessfullButUserTypeIsNotFound_thenThrowNotFoundException() {

        CustomerDto customerDto = CustomerMapper.build(user);

        OrderDto orderDto = OrderMapper.build(customerDto.getId(), paymentProcessDto);

        PaymentDto paymentDto = PaymentMapper.build(customerDto.getId(), orderDto.getId(),
                CreditCardMapper.build(paymentProcessDto.getUserPaymentInfoDto(), user.getCpf()));

        UserPaymentInfo userPaymentInfo = UserPaymentInfoMapper
                .fromDtoToEntity(paymentProcessDto.getUserPaymentInfoDto(), user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wsRaspayIntegration.createCustomer(customerDto)).thenReturn(customerDto);
        when(wsRaspayIntegration.createOrder(orderDto)).thenReturn(orderDto);
        when(wsRaspayIntegration.paymentProcess(paymentDto)).thenReturn(Boolean.TRUE);
        when(userTypeRepository.findById(UserTypeEnum.ALUNO.getId())).thenReturn(Optional.empty());

        assertEquals("UserType de nome " + UserTypeEnum.ALUNO + " não encontrado",
                assertThrows(NotFoundException.class,
                        () -> paymentInfoService.process(paymentProcessDto))
                        .getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(wsRaspayIntegration, times(1)).createCustomer(CustomerMapper.build(user));
        verify(wsRaspayIntegration, times(1)).createOrder(OrderMapper.build(customerDto.getId(), paymentProcessDto));
        verify(userPaymentInfoRepository, times(1)).save(userPaymentInfo);
        verify(userTypeRepository, times(1)).findById(UserTypeEnum.ALUNO.getId());
        verify(userCredentialsRepository, times(0)).save(any());
        verify(mailIntegration, times(0)).send(any(EmailDto.class));
        verify(userService, times(0)).updateUserSubscriptionType(any(), any());
    }

    @Test
    void givenProcess_whenPaymentProcessIsCompletlySuccessfull_thenReturnTrue() {

        ReflectionTestUtils.setField(paymentInfoService, "defaultPassword", "alunorasmoo");

        CustomerDto customerDto = CustomerMapper.build(user);

        OrderDto orderDto = OrderMapper.build(customerDto.getId(), paymentProcessDto);

        PaymentDto paymentDto = PaymentMapper.build(customerDto.getId(), orderDto.getId(),
                CreditCardMapper.build(paymentProcessDto.getUserPaymentInfoDto(), user.getCpf()));

        UserPaymentInfo userPaymentInfo = UserPaymentInfoMapper
                .fromDtoToEntity(paymentProcessDto.getUserPaymentInfoDto(), user);

        UserCredentials userCredentials = new UserCredentials(null, user.getEmail(),
                passwordEncoder.encode("alunorasmoo"),
                new UserType());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wsRaspayIntegration.createCustomer(customerDto)).thenReturn(customerDto);
        when(wsRaspayIntegration.createOrder(orderDto)).thenReturn(orderDto);
        when(wsRaspayIntegration.paymentProcess(paymentDto)).thenReturn(Boolean.TRUE);
        when(userTypeRepository.findById(UserTypeEnum.ALUNO.getId())).thenReturn(Optional.of(new UserType()));

        assertTrue(paymentInfoService.process(paymentProcessDto));

        verify(userRepository, times(1)).findById(1L);
        verify(wsRaspayIntegration, times(1)).createCustomer(CustomerMapper.build(user));
        verify(wsRaspayIntegration, times(1)).createOrder(OrderMapper.build(customerDto.getId(), paymentProcessDto));
        verify(userPaymentInfoRepository, times(1)).save(userPaymentInfo);
        verify(userTypeRepository, times(1)).findById(UserTypeEnum.ALUNO.getId());
        verify(userCredentialsRepository, times(1)).save(userCredentials);
        verify(mailIntegration, times(1)).send(any(EmailDto.class));
        verify(userService, times(1)).updateUserSubscriptionType(user, paymentProcessDto.getProductKey());
    }
}
