package com.client.ws.rasmooplus.domain.mapper.wsraspay;

import com.client.ws.rasmooplus.domain.model.jpa.User;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;

public class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerDto build(User user) {
        String[] fullName = user.getName().split(" ");

        var firstName = fullName[0];
        var lastName = fullName.length > 1 ? fullName[fullName.length - 1] : "";

        return CustomerDto.builder()
                .email(user.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .cpf(user.getCpf())
                .email(user.getEmail())
                .build();
    }
}
