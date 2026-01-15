package com.burakpozut.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GatewayConfig {

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> customerRoute() {
        return route("customer-service")
                .route(path("/api/customers/**"), http())
                .before(uri(customerServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoute() {
        return route("product-service")
                .route(path("/api/products/**"), http())
                .before(uri(productServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoute() {
        return route("order-service")
                .route(path("/api/orders/**"), http())
                .before(uri(orderServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRoute() {
        return route("payment-service")
                .route(path("/api/payments/**"), http())
                .before(uri(paymentServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationRoute() {
        return route("notification-service")
                .route(path("/api/notifications/**"), http())
                .before(uri(notificationServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return route("auth-service")
                .route(path("/api/auth/**"), http())
                .before(uri(authServiceUrl))
                .build();
    }
}
