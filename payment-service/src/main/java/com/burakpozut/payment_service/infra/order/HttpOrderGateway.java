package com.burakpozut.payment_service.infra.order;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.payment_service.domain.gateway.OrderGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpOrderGateway implements OrderGateway {
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public HttpOrderGateway(WebClient.Builder builder,
            @Value("${order.service.url}") String orderServiceUrl,
            CircuitBreaker orderCircuitBreaker,
            Retry orderRetry) {
        this.webClient = builder.baseUrl(orderServiceUrl).build();
        this.circuitBreaker = orderCircuitBreaker;
        this.retry = orderRetry;
    }

    @Override
    public void validateOrderId(UUID orderId) {
        // try {
        webClient.get().uri("/api/orders/{id}", orderId).header("X-Source-Service", "payment-service")
                .retrieve().toBodilessEntity()
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorMap(error -> mapError(error, orderId))
                .block();
    }

    private Throwable mapError(Throwable error, UUID orderId) {
        if (error instanceof WebClientResponseException.NotFound e) {
            return mapNotFoundError(e, orderId);
        } else if (error instanceof WebClientResponseException e) {
            return e.getStatusCode().is5xxServerError()
                    ? mapServerError(e, orderId)
                    : mapClientError(e, orderId);
        } else {
            return mapNetworkError(error, orderId);
        }
    }

    private ExternalServiceNotFoundException mapNotFoundError(WebClientResponseException.NotFound e, UUID orderId) {
        log.error("Payment endpoint not found for order {}: {} - {}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceNotFoundException("Payment service endpoint not found: " + e.getMessage());
    }

    private ExternalServiceException mapServerError(WebClientResponseException e, UUID orderId) {
        log.error("Payment service server error for order {}: {} - {}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Payment service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapClientError(WebClientResponseException e, UUID orderId) {
        log.error("Payment service client error for order {}: {} - {}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Payment service returned client error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID orderId) {
        log.error("Failed to communicate with Payment service for order {}: {}",
                orderId, error.getMessage());
        return new ExternalServiceException("Payment service is unavailable", error);
    }
}
