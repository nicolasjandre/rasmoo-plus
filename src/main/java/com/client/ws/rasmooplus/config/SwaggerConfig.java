package com.client.ws.rasmooplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rasmoo Plus")
                        .description("RESTful API for an educational site")
                        .version("1.0")
                        .termsOfService("Open Source")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.exemple.com")))
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Nicolas Jandre")
                                .url("http://www.exemple.com"));
    }
}
