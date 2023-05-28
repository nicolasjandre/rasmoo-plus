package com.client.ws.rasmooplus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {

    @Email(message = "Campo 'mailTo' precisa ter um email válido")
    private String mailTo;

    @NotBlank(message = "Campo 'subject' não pode estar vazio")
    private String subject;

    @NotBlank(message = "Campo 'message' não pode estar vazio")
    private String message;
}
