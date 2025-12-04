package com.reservas.facisa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Reservas de Salas - UNIFACISA")
                        .description("API REST para cadastro de salas, usuários, reservas e consulta de disponibilidade.")
                        .version("1.0.0"));
    }
}
