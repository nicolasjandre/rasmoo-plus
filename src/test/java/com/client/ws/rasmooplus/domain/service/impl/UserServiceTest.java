package com.client.ws.rasmooplus.domain.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.client.ws.rasmooplus.domain.exception.BadRequestException;
import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.model.jpa.SubscriptionType;
import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.domain.model.jpa.UserType;
import com.client.ws.rasmooplus.domain.repository.jpa.SubscriptionTypeRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserRepository;
import com.client.ws.rasmooplus.domain.repository.jpa.UserTypeRepository;
import com.client.ws.rasmooplus.dto.UserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private SubscriptionTypeRepository subscriptionTypeRepository;

    private UserDto userDto;

    @BeforeEach
    public void loadUser() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("nicolasjandre@live.com");
        userDto.setCpf("17045898788");
        userDto.setUserTypeId(1L);
    }

    @Test
    void givenCreate_whenIdIsNullAndUserIsFound_thenReturnUserCreated() {

        UserType userType = new UserType(1L, "Aluno", "Aluno da plataforma");

        userDto.setId(null);

        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(userType));

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setCpf(userDto.getCpf());
        user.setDtExpiration(userDto.getDtExpiration());
        user.setDtSubscription(userDto.getDtSubscription());
        user.setUserType(userType);

        when(userRepository.save(user)).thenReturn(user);

        Assertions.assertEquals(user, userService.create(userDto));

        verify(userTypeRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenCreate_whenIdIsNotNull_thenThrowBadRequestException() {

        Assertions.assertThrows(BadRequestException.class, () -> userService.create(userDto));

        verify(userTypeRepository, times(0)).findById(any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void givenCreate_whenIdIsNullAndUserIsNotFound_thenReturnUserCreated() {

        userDto.setId(null);

        when(userTypeRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.create(userDto));

        verify(userTypeRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void givenUpdateUserSubscriptionType_whenUserIdIsNull_thenThrowBadRequestException() {

        User user = new User();
        user.setId(null);

        Assertions.assertThrows(BadRequestException.class, () -> userService.updateUserSubscriptionType(user, ""));

        verify(userRepository, times(0)).findById(any());
        verify(subscriptionTypeRepository, times(0)).findByProductKey(any());
    }

    @Test
    void givenUpdateUserSubscriptionType_whenUserIdIsNotNullButUserIsNotFound_thenThrowNotFoundException() {
        
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserSubscriptionType(user, ""));

        verify(userRepository, times(1)).findById(1L);
        verify(subscriptionTypeRepository, times(0)).findByProductKey(any());
    }

    @Test
    void givenUpdateUserSubscriptionType_whenProductKeyIsNotFound_thenThrowNotFoundException() {
        
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionTypeRepository.findByProductKey("MONTH25")).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserSubscriptionType(user, "MONTH25"));

        verify(userRepository, times(1)).findById(1L);
        verify(subscriptionTypeRepository, times(1)).findByProductKey("MONTH25");
    }

    @Test
    void givenUpdateUserSubscriptionType_whenUserAndSubscriptionTypeIsOk_thenDontThrowAnyErrorAndJustReturnVoid() {
        
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionTypeRepository.findByProductKey("MONTH22")).thenReturn(Optional.of(new SubscriptionType()));

        Assertions.assertDoesNotThrow(() -> userService.updateUserSubscriptionType(user, "MONTH22"));

        verify(userRepository, times(1)).findById(1L);
        verify(subscriptionTypeRepository, times(1)).findByProductKey("MONTH22");
        verify(userRepository, times(1)).save(user);
    }
}
