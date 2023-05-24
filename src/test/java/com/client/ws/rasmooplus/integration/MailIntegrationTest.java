package com.client.ws.rasmooplus.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailIntegrationTest {

    @Autowired
    private MailIntegration mailIntegration;

    @Test
    void sendEmail() {
        String message = String.format("""
                Seja bem vindo!
                Nós da Rasmoo desejamos sucesso nessa sua caminhada, tenha um excelente estudo!

                Usuário: nicolasjandre@live.com
                Senha: alunorasmoo
                    """);

        mailIntegration.send("nicolasjandre@live.com", "Acesso liberado!", message);
    }
}
