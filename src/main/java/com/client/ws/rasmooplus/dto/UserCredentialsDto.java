package com.client.ws.rasmooplus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsDto {
    
    @Email(message = "Campo 'email' precisa conter um email válido")
    private String email;

    @NotBlank(message = "Campo 'password' não pode estar vazio")
    private String password;

    @NotBlank(message = "Campo 'passwordConfirmation' não pode estar vazio")
    private String passwordConfirmation;
}
