package com.burakpozut.order_service.infra.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpPaymentGateway implements PaymentGateway {
    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    private record CreatePaymentRequest(
            UUID orderId,
            BigDecimal amount,
            Currency currency,
            String provider,
            String providerRef) {
    }

    public HttpPaymentGateway(RestClient.Builder builder,
            @Value("${payment.service.url}") String paymentServiceUrl,
            CircuitBreaker paymentCircuitBreaker,
            Retry paymentRetry) {
        this.restClient = builder.baseUrl(paymentServiceUrl).build();
        this.circuitBreaker = paymentCircuitBreaker;
        this.retry = paymentRetry;
    }

    @Override
    public void createPayment(UUID orderId, BigDecimal amount, Currency currency, String provider, String providerRef) {
        var request = new CreatePaymentRequest(orderId, amount,
                currency, provider, providerRef);

        try {
            retry.executeRunnable(() -> circuitBreaker.executeRunnable(() -> {
                restClient.post()
                        .uri("/api/payments")
                        .header("X-Source-Service", "order-service")
                        .header("X-Trace-Id", safeTraceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .toBodilessEntity();
            }));
        } catch (CallNotPermittedException e) {
            handleCircuitBreakerOpenFallback(orderId);
        } catch (RestClientResponseException e) {
            throw mapResponseError(e, orderId);
        } catch (Exception e) {
            throw mapNetworkError(e, orderId);
        }
    }

    private String safeTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "" : traceId;
    }

    private void handleCircuitBreakerOpenFallback(UUID orderId) {
        log.warn("payment.service.circuit_breaker_open orderId={} action=skip_payment",
                orderId);
        throw new ExternalServiceException(
                "Payment service circuit breaker is open - cannot create payment: " + orderId);
    }

    private RuntimeException mapResponseError(RestClientResponseException e, UUID orderId) {
        if (e.getStatusCode().is4xxClientError()) {
            if (e.getStatusCode().value() == 404) {
                log.error("payment.endpoint.not_found orderId={} statusCode={} message={}",
                        orderId, e.getStatusCode(), e.getMessage());
                return new ExternalServiceNotFoundException("Payment service endpoint not found: " + e.getMessage());
            }
            log.error("payment.service.client_error orderId={} statusCode={} message={}",
                    orderId, e.getStatusCode(), e.getMessage());
            return new ExternalServiceException("Payment service returned client error: " + e.getStatusCode(), e);
        }

        log.error("payment.service.server_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Payment service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID orderId) {
        log.error("payment.service.communication_failed orderId={} message={}",
                orderId, error.getMessage());
        return new ExternalServiceException("Payment service is unavailable", error);
    }
}