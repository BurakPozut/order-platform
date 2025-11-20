package com.burakpozut.microservices.order_platform_monolith.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Order Platform API").description("API documentation for Order Platform monolith")
            .contact(new Contact().name("Order Platform Team").email("suport@orderplatform.com"))
            .license(new License().name("Apache 2.0").url("https://www.apache.org")));
  }

}
