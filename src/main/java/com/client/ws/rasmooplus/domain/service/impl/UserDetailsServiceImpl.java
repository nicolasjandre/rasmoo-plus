package com.client.ws.rasmooplus.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.client.ws.rasmooplus.domain.exception.NotFoundException;
import com.client.ws.rasmooplus.domain.repository.UserCredentialsRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var userDetailsOpt = userCredentialsRepository.findByUsername(username);

        if (userDetailsOpt.isEmpty()) {
            throw new NotFoundException("Usuário ou senha inválidos");
        }

        return userDetailsOpt.get();
    }
}
