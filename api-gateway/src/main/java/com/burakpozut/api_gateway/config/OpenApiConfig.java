package com.burakpozut.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway - Microservices")
                        .version("1.0.0")
                        .description("API Gateway for E-Commerce Microservices Platform")
                        .contact(new Contact()
                                .name("API Gateway")
                                .email("support@example.com")));
    }
}