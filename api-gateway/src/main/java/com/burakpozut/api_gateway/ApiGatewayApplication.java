package com.burakpozut.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
// Do this in order:
// 1. JWT validation in services (Resource Server) + gateway pass-through
// 2. Identity service issues access token
// 3. Add refresh token rotation + hashed storage
// 4. Add audience checks + a simple scope model
// 5. Add one real authorization rule (ownership)
