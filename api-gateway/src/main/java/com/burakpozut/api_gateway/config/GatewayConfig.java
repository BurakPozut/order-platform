package com.burakpozut.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> customerRoute() {
        return route("customer-service")
                .route(path("/api/customers/**"), http())
                .before(uri("http://localhost:9081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoute() {
        return route("product-service")
                .route(path("/api/products/**"), http())
                .before(uri("http://localhost:9082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoute() {
        return route("order-service")
                .route(path("/api/orders/**"), http())
                .before(uri("http://localhost:9083"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRoute() {
        return route("payment-service")
                .route(path("/api/payments/**"), http())
                .before(uri("http://localhost:9084"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationRoute() {
        return route("notification-service")
                .route(path("/api/notifications/**"), http())
                .before(uri("http://localhost:9085"))
                .build();
    }
}
