package com.client.ws.rasmooplus.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Campo 'username' não pode estar vazio")
    private String username;

    @NotBlank(message = "Campo 'password' não pode estar vazio")
    private String password;
}
