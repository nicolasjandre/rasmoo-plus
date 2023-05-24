package com.client.ws.rasmooplus.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Campo 'name' não pode ser nulo ou vazio")
    @Size(min = 6, message = "Campo 'name' precisa ter ao menos 6 caracteres")
    private String name;

    @Email(message = "Campo 'email' precisa conter um email válido")
    private String email;

    @Size(min = 11, message = "Campo 'phone' precisa ter um número válido com ao menos 11 caracteres")
    private String phone;

    @CPF(message = "Campo 'CPF' precisa conter um CPF válido")
    private String cpf;

    @Builder.Default
    private LocalDate dtSubscription = LocalDate.now();

    @Builder.Default
    private LocalDate dtExpiration = LocalDate.now();

    @NotNull(message = "Campo 'userTypeId' não pode ser nulo")
    private Long userTypeId;

    private Long subscriptionTypeId;
}
