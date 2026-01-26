package com.burakpozut.order_service.infra.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpPaymentGateway implements PaymentGateway {
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    private record CreatePaymentRequest(
            UUID orderId,
            BigDecimal amount,
            Currency currency,
            String provider,
            String providerRef) {
    }

    public HttpPaymentGateway(WebClient.Builder builder,
            @Value("${payment.service.url}") String paymentServiceUrl,
            CircuitBreaker paymentCircuitBreaker,
            Retry paymentRetry) {
        this.webClient = builder.baseUrl(paymentServiceUrl).build();
        this.circuitBreaker = paymentCircuitBreaker;
        this.retry = paymentRetry;
    }

    @Override
    public void createPayment(UUID orderId, BigDecimal amount, Currency currency, String provider, String providerRef) {
        var request = new CreatePaymentRequest(orderId, amount,
                currency, provider, providerRef);
        webClient.post().uri("/api/payments").header("X-Source-Service", "order-service")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
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
        log.error("payment.endpoint.not_found orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceNotFoundException("Payment service endpoint not found: " + e.getMessage());
    }

    private ExternalServiceException mapServerError(WebClientResponseException e, UUID orderId) {
        log.error("payment.service.server_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Payment service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapClientError(WebClientResponseException e, UUID orderId) {
        log.error("payment.service.client_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Payment service returned client error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID orderId) {
        log.error("payment.service.communication_failed orderId={} message={}",
                orderId, error.getMessage());
        return new ExternalServiceException("Payment service is unavailable", error);
    }
}
