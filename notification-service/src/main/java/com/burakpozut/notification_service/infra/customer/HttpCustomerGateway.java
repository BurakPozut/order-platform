package com.burakpozut.notification_service.infra.customer;

import java.util.UUID;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.notification_service.domain.gateway.CustomerGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpCustomerGateway implements CustomerGateway {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public HttpCustomerGateway(WebClient.Builder builder,
            @Value("${customer.service.url}") String customerServiceUrl,
            CircuitBreaker customerCircuitBreaker,
            Retry customerRetry) {
        this.webClient = builder.baseUrl(customerServiceUrl).build();
        this.circuitBreaker = customerCircuitBreaker;
        this.retry = customerRetry;
    }

    @Override
    public void validateCustomerExists(UUID customerId) {
        webClient.get().uri("api/customers/{id}",
                customerId).header("X-Source-Service", "notification-service").retrieve().toBodilessEntity()
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorMap(error -> mapError(error, customerId))
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

    private ExternalServiceNotFoundException mapNotFoundError(WebClientResponseException.NotFound e, UUID customerId) {
        log.error("Customer endpoint not found for customer {}: {} - {}",
                customerId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceNotFoundException("Customer service endpoint not found: " + e.getMessage());
    }

    private ExternalServiceException mapServerError(WebClientResponseException e, UUID customerId) {
        log.error("Customer service server error for customer {}: {} - {}",
                customerId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Customer service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapClientError(WebClientResponseException e, UUID customerId) {
        log.error("Customer service client error for customer {}: {} - {}",
                customerId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Customer service returned client error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID customerId) {
        log.error("Failed to communicate with Customer service for customer {}: {}",
                customerId, error.getMessage());
        return new ExternalServiceException("Customer service is unavailable", error);
    }
}
